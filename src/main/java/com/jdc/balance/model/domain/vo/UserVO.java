package com.jdc.balance.model.domain.vo;

import com.jdc.balance.model.domain.entity.User;

public class UserVO {

	private int id;
	private String name;
	private String loginId;
	private String phone;
	private String email;
	private boolean status;
	
	public UserVO() {
		// TODO Auto-generated constructor stub
	}
	
	public UserVO(User u) {
		this.id = u.getId();
		this.name = u.getName();
		this.loginId = u.getLoginId();
		this.phone = u.getPhone();
		this.email = u.getEmail();
		this.status = u.isActive();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

}