package com.jdc.balance.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.jdc.balance.model.domain.entity.User;
import com.jdc.balance.model.domain.entity.User.Role;
import com.jdc.balance.model.repo.UserRepo;

@Component
public class AppUserInitializer {
	
	@Autowired
	private PasswordEncoder encoder;
	@Autowired
	private UserRepo userRepo;
	
	@EventListener(classes = ContextRefreshedEvent.class)
	public void initializedUser() {
		if(userRepo.count() == 0) {
			var user = new User();
			user.setName("Admin User");
			user.setLoginId("admin");
			user.setPassword(encoder.encode("admin"));
			user.setRole(Role.Admin);
			user.setActive(true);
			userRepo.save(user);	
		}
		
	}

}
