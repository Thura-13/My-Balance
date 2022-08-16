package com.jdc.balance.controller;

import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jdc.balance.model.domain.vo.UserVO;
import com.jdc.balance.model.service.UserAccessLogService;
import com.jdc.balance.model.service.UserService;
import com.jdc.balance.utils.Pagination;

@Controller
@RequestMapping("user/profile")
public class ProfileController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	UserAccessLogService userAccessLogService;

	@GetMapping
	public String index(
			ModelMap model,
			@RequestParam Optional<Integer> page,
			@RequestParam Optional<Integer> size) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		UserVO user = userService.findById(username);
		model.put("user", user);
		
		var accessLog = userAccessLogService.search(username,page.orElse(0),size.orElse(5));
		var list = accessLog.getContent();
		 
		model.put("list", list);
		var param = new HashMap<String,String>();
		var pagination = Pagination.builder("/user/profile")
				.pages(accessLog).params(param).build();
		
		model.put("pagination", pagination);
		
		return "profile";
	}
	
	@PostMapping("contact")
	public String updateContact(@RequestParam String phone,@RequestParam String email) {
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		userService.updateContact(username,phone,email);
		return "redirect:/user/profile";
	}
}
