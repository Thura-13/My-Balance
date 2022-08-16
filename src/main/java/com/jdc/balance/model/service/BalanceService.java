package com.jdc.balance.model.service;

import java.time.LocalDate;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.jdc.balance.model.domain.entity.Balance;
import com.jdc.balance.model.domain.entity.Balance.Type;
import com.jdc.balance.model.domain.entity.BalanceItem;
import com.jdc.balance.model.domain.form.BalanceEditForm;
import com.jdc.balance.model.domain.vo.BalanceReportVO;
import com.jdc.balance.model.repo.BalanceItemRepo;
import com.jdc.balance.model.repo.BalanceRepo;
import com.jdc.balance.model.repo.UserRepo;

@Service
public class BalanceService {

	@Autowired
	private BalanceItemRepo itemRepo;
	@Autowired
	private BalanceRepo balanceRepo;
	@Autowired
	private UserRepo userRepo;

	@PreAuthorize("authenticated()")
	public Page<BalanceItem> search(Type type, String keyword, LocalDate dateFrom, LocalDate dateTo,
			Optional<Integer> size, Optional<Integer> page) {

		var pageRequest = PageRequest.of(page.orElse(0), size.orElse(10));

		String username = SecurityContextHolder.getContext().getAuthentication().getName();

//		For Login User
		Specification<BalanceItem> spec = (root, query, builder) -> builder
				.equal(root.get("balance").get("user").get("loginId"), username);

//				Type

		spec = spec.and((root, query, builder) -> builder.equal(root.get("balance").get("type"), type));

//				Date From

		if (null != dateFrom) {
			spec = spec.and(
					(root, query, builder) -> builder.greaterThanOrEqualTo(root.get("balance").get("date"), dateFrom));
		}
//				Date To

		if (null != dateTo) {
			spec = spec
					.and((root, query, builder) -> builder.lessThanOrEqualTo(root.get("balance").get("date"), dateTo));
		}

//				Keyword

		if (StringUtils.hasLength(keyword)) {

			Specification<BalanceItem> category = (root, query, builder) -> builder.like(
					builder.lower(root.get("balance").get("category")), "%%%s%%".formatted(keyword.toLowerCase()));

			Specification<BalanceItem> item = (root, query, builder) -> builder.like(builder.lower(root.get("item")),
					"%%%s%%".formatted(keyword.toLowerCase()));

			spec = spec.and(category.or(item));
		}

		return itemRepo.findAll(spec, pageRequest);
	}

	public BalanceEditForm findById(int id) {
		var balance = balanceRepo.findById(id).orElseThrow();
		return new BalanceEditForm(balance);
	}

	@Transactional
	public int save(BalanceEditForm balanceEditForm) {

		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		var user = userRepo.findOneByLoginId(username).orElseThrow();

		var balance = balanceEditForm.getHeader().getId() == 0 ? new Balance()
				: balanceRepo.findById(balanceEditForm.getHeader().getId()).orElseThrow();

		balance.setUser(user);
		balance.setCategory(balanceEditForm.getHeader().getCategory());
		balance.setDate(balanceEditForm.getHeader().getDate());
		balance.setType(balanceEditForm.getHeader().getType());

		balance = balanceRepo.save(balance);

		for (var formItem : balanceEditForm.getItems()) {

			var item = formItem.getId() == 0 ? new BalanceItem() : itemRepo.findById(formItem.getId()).orElseThrow();

			if (formItem.isDeleted()) {
				itemRepo.delete(item);
				continue;
			}
			item.setBalance(balance);
			item.setItem(formItem.getItem());
			item.setQuantity(formItem.getQuantity());
			item.setUnitPrice(formItem.getUnitPrice());

			itemRepo.save(item);
		}
		return balance.getId();
	}

	public void deleteById(int id) {
		balanceRepo.deleteById(id);
	}

	@PreAuthorize("authenticated()")
	public Page<BalanceReportVO> searchBalanceReport(Type type, LocalDate dateFrom, LocalDate dateTo,
			Optional<Integer> size, Optional<Integer> page) {

		Specification<Balance> spec = (root, query, builder) -> builder.equal(root.get("user").get("loginId"),
				SecurityContextHolder.getContext().getAuthentication().getName());
		PageRequest pageInfo = PageRequest.of(page.orElse(0), size.orElse(5));

		if (null != type) {
			spec = spec.and((root, query, builder) -> builder.equal(root.get("type"), type));
		}

		if (null != dateFrom) {
			spec = spec.and((root, query, builder) -> builder.greaterThanOrEqualTo(root.get("date"), dateFrom));
		}

		if (null != dateTo) {
			spec = spec.and((root, query, builder) -> builder.lessThanOrEqualTo(root.get("date"), dateTo));
		}

//		Net Balance Calculation

		var result = balanceRepo.findAll(spec, pageInfo).map(BalanceReportVO::new);

		var lastBalance = 0;
		
		if(!result.getContent().isEmpty()) {
			
			for (var vo : result.getContent()) {
				
				if (vo.getType() == Type.Income) {
					lastBalance += vo.getAmount();
				}else {
					lastBalance -= vo.getAmount();
				}
				
				vo.setBalance(lastBalance);
			}
		}

		return result;
	}

}
