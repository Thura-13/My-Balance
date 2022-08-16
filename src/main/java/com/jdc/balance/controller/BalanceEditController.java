package com.jdc.balance.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.jdc.balance.model.BalanceAppException;
import com.jdc.balance.model.domain.entity.Balance.Type;
import com.jdc.balance.model.domain.form.BalanceEditForm;
import com.jdc.balance.model.domain.form.BalanceItemForm;
import com.jdc.balance.model.domain.form.BalanceSummaryForm;
import com.jdc.balance.model.service.BalanceService;

@Controller
@RequestMapping("user/balance-edit")
@SessionAttributes("balanceEditForm")
public class BalanceEditController {
	
	@Autowired
	private BalanceService balanceService;

	@PostMapping("save")
	public String save(@ModelAttribute("balanceEditForm") BalanceEditForm balanceEditForm,
			@ModelAttribute("summaryForm")@Valid BalanceSummaryForm summaryForm,BindingResult br) {
		
		if(br.hasErrors()) {
			return "balance-edit-confirm";
		}
		
		balanceEditForm.getHeader().setCategory(summaryForm.getCategory());
		balanceEditForm.getHeader().setDate(summaryForm.getDate());
		
		int id = balanceService.save(balanceEditForm);
		
		balanceEditForm.clear();
		return "redirect:/user/balance/detail/%d".formatted(id);
	}

	@GetMapping
	public String edit(@ModelAttribute("balanceEditForm") BalanceEditForm form,
			@RequestParam(required = false) Integer id,
			@RequestParam(required = false) Type type) {
		
		if(null != id ) {
			var result = balanceService.findById(id);
			
			form.setHeader(result.getHeader());
			form.setItems(result.getItems());
		}
		
		if(null != type && form.getHeader().getType() != type) {
			form.setHeader(new BalanceSummaryForm());
			form.getHeader().setType(type);
			form.getItems().clear();
		}
		return "balance-edit";
	}
	
	@GetMapping("confirm")
	public String confirm() {
		return "balance-edit-confirm";
	}
	
	@PostMapping("add-item")
	public String add(@ModelAttribute("balanceEditForm")BalanceEditForm form,
			@ModelAttribute("itemForm")@Valid BalanceItemForm itemForm,
			BindingResult result) {
		
		if(result.hasErrors()) {
			return "balance-edit";
		}
		form.getItems().add(itemForm);
		return "redirect:/user/balance-edit/?type=%s".formatted(form.getHeader().getType());
	}
	
	@GetMapping("delete-item")
	public String deleteItem(@ModelAttribute("balanceEditForm")BalanceEditForm form,@RequestParam int index) {
		var item = form.getItems().get(index);
		
		if(item.getId() == 0) {
			form.getItems().remove(index);
		}else {
			item.setDeleted(true);
		}
		return "redirect:/user/balance-edit/?type=%s".formatted(form.getHeader().getType());
		
	}
	
	
	@ModelAttribute("itemForm")
	public BalanceItemForm form() {
		return new BalanceItemForm();
	}
	
	@ModelAttribute("summaryForm")
	public BalanceSummaryForm summaryForm(@ModelAttribute("balanceEditForm") BalanceEditForm form) {
		return form.getHeader();
	}
	
	@ModelAttribute("balanceEditForm")
	public BalanceEditForm form(@RequestParam(required = false) Integer id,@RequestParam(required = false)Type type) {
		
		
		if(id != null) {
			return balanceService.findById(id);
		}
		
		if(type == null) {
			throw new BalanceAppException("Please set category for balance");
		}
		
		return new BalanceEditForm().type(type);
	}

}
