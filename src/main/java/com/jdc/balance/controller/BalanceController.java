package com.jdc.balance.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jdc.balance.model.domain.entity.Balance.Type;
import com.jdc.balance.model.service.BalanceService;
import com.jdc.balance.utils.Pagination;

@Controller
@RequestMapping("user/balance")
public class BalanceController {
	
	@Autowired
	private BalanceService balanceService;
	
	DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	@GetMapping
	public String balanceList(
			ModelMap model,
			@RequestParam(required = false) Type type,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFrom,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateTo,
			@RequestParam Optional<Integer>size,
			@RequestParam Optional<Integer>page
			) {
		
		var result = balanceService.searchBalanceReport(type,dateFrom,dateTo,size,page);
		
		var pagination = Pagination.builder("user/balance")
				.pages(result)
				.params(
						Map.of("type",null == type ? "" : type.name(),
								"dateFrom", null == dateFrom ? "" : dateFrom.format(df),
								"dateTo", null ==dateTo ? "" : dateTo.format(df)		))
				.build();
		
		model.put("list", result.getContent());
		model.put("paginatoin", pagination);
		
		return "balance-report";
	}
	
	@GetMapping("{type}")
	public String searchBalanceItems(
			ModelMap model,
			@PathVariable Type type,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateFrom,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateTo,
			@RequestParam Optional<Integer>size,
			@RequestParam Optional<Integer>page
			) {
		
		var result = balanceService.search(type,keyword,dateFrom,dateTo,size,page);
		
		model.put("title"," %s Management".formatted(type));
		model.put("type",type);
		
		
		var param = new HashMap<String,String>();
		param.put("keyword", StringUtils.hasLength(keyword) ? keyword : "");
		param.put("dateFrom", dateFrom == null ? "" : dateFrom.format(df));
		param.put("dateTo", dateTo == null ? "" : dateTo.format(df));
		
		var pagination = Pagination.builder("/user/balance")
				.pages(result)
				.params(param)
				.sizes(List.of(5,10,15))
				.build();
		
		model.put("pagination", pagination);
		model.put("list", result.getContent());

		return "balance-list";
	}

	
	@GetMapping("delete/{id:\\d+}")
	public String delete(@PathVariable int id) {
		balanceService.deleteById(id);
		return "redirect:/";
	}

	@GetMapping("detail/{id:\\d+}")
	public String findById(ModelMap model,@PathVariable int id) {
		model.put("vo", balanceService.findById(id));
		return "balance-detail";
	}

	public String search( String category, LocalDate from, LocalDate to) {
		return "";
	}
	
	@ModelAttribute("balanceType")
	Type[] type() {
		return Type.values();
	}
}