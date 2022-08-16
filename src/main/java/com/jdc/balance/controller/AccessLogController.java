package com.jdc.balance.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jdc.balance.model.domain.entity.UserAccessLog.Type;
import com.jdc.balance.model.service.UserAccessLogService;
import com.jdc.balance.utils.Pagination;

@Controller
@RequestMapping("admin/access")
public class AccessLogController {

	@Autowired
	private UserAccessLogService service;

	@GetMapping
	public String search(@RequestParam(required = false) Type type, @RequestParam(required = false) String userName,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
			Optional<Integer> page, Optional<Integer> size, ModelMap model) {

		var result = service.searchAccessLogByAdmin(type, userName, date, page, size);
		model.put("list", result);
		
		var params = new HashMap<String,String>();
		
		params.put("type", type == null ? "" : type.name());
		params.put("userName", StringUtils.hasLength(userName) ? userName : "");
		params.put("date", date == null ? "" : date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		
		model.put("pager", Pagination.builder("/admin/access")
				.pages(result)
				.params(params)
				.sizes(List.of(5,10,15))
				.build());
		return "access-log";
	}

	@ModelAttribute(name = "types")
	Type[] type() {
		return Type.values();
	}
}
