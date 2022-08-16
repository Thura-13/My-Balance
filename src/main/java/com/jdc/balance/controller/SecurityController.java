package com.jdc.balance.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jdc.balance.model.domain.entity.User.Role;
import com.jdc.balance.model.domain.form.ChangePasswordForm;
import com.jdc.balance.model.domain.form.SignUpForm;
import com.jdc.balance.model.service.UserService;

@Controller
public class SecurityController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/")
	public String index() {
		var auths =  SecurityContextHolder.getContext().getAuthentication();
		if(auths != null && auths.getAuthorities()
				.stream()
				.anyMatch(auth -> 
				auth.getAuthority().equals(Role.Admin.name())
				||auth.getAuthority().equals(Role.Member.name())
				)) {
			
			return "redirect:/user/balance";
		}
		
		return "sign-in";
	}

	@PostMapping("signup")
    public String SignUp(@ModelAttribute(name = "form") @Valid SignUpForm form,BindingResult result) {
		
		if(result.hasErrors()) {
			return "sign-up";
		}
		
		userService.signUp(form);
		return "redirect:/";
    }

	@GetMapping("signup")
    public String loadSignUp(@ModelAttribute(name = "form")SignUpForm form) {
		return "sign-up";
    }
	
    
    @PostMapping("/user/changePass")
    public String changePass(@ModelAttribute ChangePasswordForm form,RedirectAttributes rd) {
    	userService.changePassword(form);
    	rd.addFlashAttribute("message","Your Password  Has Been Change Successfully!");
    	return "redirect:/";
    }

}