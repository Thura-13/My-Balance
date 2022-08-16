package com.jdc.balance.model.domain.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.jdc.balance.model.domain.entity.Balance;
import com.jdc.balance.model.domain.entity.Balance.Type;

public class BalanceEditForm implements Serializable {

	private static final long serialVersionUID = 1L;

	private BalanceSummaryForm header;
	private List<BalanceItemForm> items;

	public BalanceEditForm() {
		header = new BalanceSummaryForm();
		items = new ArrayList<BalanceItemForm>();
	}

	public BalanceEditForm(Balance balance) {
		header = new BalanceSummaryForm();
		header.setId(balance.getId());
		header.setCategory(balance.getCategory());
		header.setDate(balance.getDate());
		header.setType(balance.getType());

		items = new ArrayList<BalanceItemForm>(balance.getItems().stream().map(a -> {
			var item = new BalanceItemForm();
			item.setId(a.getId());
			item.setItem(a.getItem());
			item.setQuantity(a.getQuantity());
			item.setUnitPrice(a.getUnitPrice());
			return item;
		}).toList());

	}

	public BalanceEditForm type(Type type) {
		header.setType(type);
		return this;
	}

	public BalanceSummaryForm getHeader() {
		return header;
	}

	public void setHeader(BalanceSummaryForm header) {
		this.header = header;
	}

	public List<BalanceItemForm> getItems() {
		return items;
	}

	public void setItems(List<BalanceItemForm> items) {
		this.items = items;
	}

	public int getTotal() {
		return items.stream().filter(a->!a.isDeleted()).mapToInt(a -> a.getQuantity() * a.getUnitPrice()).sum();
	}

	public int getQuantityTotal() {
		return items.stream().filter(a->!a.isDeleted()).mapToInt(a -> a.getQuantity()).sum();
	}

	public boolean isShowNextBtn() {
		return !items.isEmpty();
	}
	
	public List<BalanceItemForm> validItem() {
		return items.stream().filter(a->!a.isDeleted()).toList();
	}

	public void clear() {
		items.clear();
		header = new BalanceSummaryForm();
	}

}