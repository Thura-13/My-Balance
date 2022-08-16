package com.jdc.balance.model.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.jdc.balance.model.domain.entity.UserAccessLog;
import com.jdc.balance.model.domain.entity.UserAccessLog.Type;
import com.jdc.balance.model.repo.UserAccessLogRepo;

@Service
public class UserAccessLogService {

	@Autowired
	private UserAccessLogRepo userAccessLogRepo;

	public Page<UserAccessLog> search(String username, Integer page, Integer size) {
		
		Specification<UserAccessLog> spec = (root,query,builder) -> builder.equal(root.get("username"), username);
		PageRequest pageable = PageRequest.of(page, size).withSort(Sort.by("accessTime").descending());
		return userAccessLogRepo.findAll(spec, pageable);
	}

	public Page<UserAccessLog> searchAccessLogByAdmin(Type type, String userName, LocalDate date, Optional<Integer> page,
			Optional<Integer> size) {
		
		var pageable = PageRequest.of(page.orElse(0), size.orElse(5)).withSort(Sort.by("accessTime").descending());
		
		Specification< UserAccessLog> spec = Specification.where(null);
		
		if( null != type) {
			spec = spec.and((root,query,builder)->builder.equal(root.get("type"), type));
		}
		
		if(StringUtils.hasLength(userName)) {
			spec = spec.and((root, query, builder) -> builder.like(builder.lower(root.get("username")),
					userName.toLowerCase().concat("%")));
		}
		
		if(null != date) {
			spec = spec.and((root, query, builder) -> builder.greaterThanOrEqualTo(root.get("accessTime"), date.atStartOfDay()));
		}
		
		return userAccessLogRepo.findAll(spec, pageable);
	}
}
