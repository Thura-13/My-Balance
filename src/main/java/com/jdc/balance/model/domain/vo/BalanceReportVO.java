package com.jdc.balance.model.domain.vo;

import com.jdc.balance.model.domain.entity.Balance;
import com.jdc.balance.model.domain.entity.Balance.Type;
import java.time.LocalDate;

public class BalanceReportVO {

	private int id;
	private LocalDate date;
	private Type type;
	private String category;
	private int amount;
	private int balance;
	
	public BalanceReportVO() {
	}

	public BalanceReportVO(Balance balance) {
		this.id = balance.getId();
		this.date = balance.getDate();
		this.type = balance.getType();
		this.category = balance.getCategory();
		this.amount = balance.getItems().stream().mapToInt(a->a.getQuantity() * a.getUnitPrice()).sum();
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}
	
	public int getIncome() {
		return type == Type.Income ? amount : 0;
	}
	
	public int getExpense() {
		return type == Type.Expense ? amount : 0;
	}

}