package com.jdc.balance.model.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.jdc.balance.model.BalanceAppException;
import com.jdc.balance.model.domain.entity.User;
import com.jdc.balance.model.domain.entity.User.Role;
import com.jdc.balance.model.domain.form.ChangePasswordForm;
import com.jdc.balance.model.domain.form.SignUpForm;
import com.jdc.balance.model.domain.vo.UserVO;
import com.jdc.balance.model.repo.UserRepo;

@Service
public class UserService {
	
	
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private PasswordEncoder encoder;


	@Transactional
	public void signUp(SignUpForm form) {
		var user = new User(form);
		user.setPassword(encoder.encode(user.getPassword()));
		userRepo.save(user);
	}

	public UserVO findById(String username) {
		return userRepo.findOneByLoginId(username).map(u->new UserVO(u)).orElseThrow();
	}

	@Transactional
	public void updateContact(String username,String phone, String email) {
		
		userRepo.findOneByLoginId(username)
		.ifPresent(user ->{
			 user.setPhone(phone);
			 user.setEmail(email);
		});
	}


	public List<UserVO> search(Boolean status, String name, String phone) {
		
		Specification<User> spec = (root,query,builder) -> builder.equal(root.get("role"), Role.Member);
		
		if(null != status) {
			spec = spec.and((root,query,builder) -> builder.equal(root.get("active"), status));
		}
		
		if(StringUtils.hasLength(name)) {
			spec = spec.and((root,query,builder) -> builder.like(builder.lower(root.get("name")), name.toLowerCase().concat("%")));
		}
		
		
		if(StringUtils.hasLength(phone)) {
			spec = spec.and((root,query,builder) -> builder.like(root.get("phone"), phone.concat("%")));
		}
		
		return userRepo.findAll(spec).stream().map(UserVO:: new).toList();
	}

	@Transactional
	public void changeStatus(int loginId, Boolean status) {
		
		userRepo.findById(loginId).ifPresent(u -> u.setActive(status));
		
	}

	@Transactional
	public void changePassword(ChangePasswordForm form) {
		
		if(!StringUtils.hasLength(form.getNewPassword())) {
			throw new BalanceAppException("Please Enter New Password");
		}
		
		if(!StringUtils.hasLength(form.getOldPassword())) {
			throw new BalanceAppException("Please Enter Old Password");
		}
		
		var user = userRepo.findOneByLoginId(form.getLoginId()).orElseThrow();
		
		if(!encoder.matches(form.getOldPassword(), user.getPassword())) {
			throw new BalanceAppException("Please Check Yout Old Password");
		}
		
		if(form.getOldPassword().equals(form.getNewPassword())) {
			throw new BalanceAppException("Please Enter different New Password");
		}
		
		user.setPassword(encoder.encode(form.getNewPassword()));
	}

}
