package com.jdc.balance.security;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;
import org.springframework.stereotype.Component;

import com.jdc.balance.model.domain.entity.UserAccessLog;
import com.jdc.balance.model.domain.entity.UserAccessLog.Type;
import com.jdc.balance.model.repo.UserAccessLogRepo;

@Component
public class BalanceAppAccessListener {
	
	@Autowired
	private UserAccessLogRepo userAccessLogRepo;

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@EventListener
	@Transactional
	void onSuccess(AuthenticationSuccessEvent event) {
		
		var username = event.getAuthentication().getName();
		var time = LocalDateTime.ofInstant(
				new Date(event.getTimestamp()).toInstant(), ZoneId.systemDefault());
		logger.info("{} is sign in at {}",username,time);
		userAccessLogRepo.save(new UserAccessLog(username, Type.Signin, time));
		
	}
	
	@EventListener
	@Transactional
	void onFailure(AbstractAuthenticationFailureEvent event) {
		var username = event.getAuthentication().getName();
		var time = LocalDateTime.ofInstant(
				new Date(event.getTimestamp()).toInstant(), ZoneId.systemDefault());
		logger.info("{} is fail sign in at {} because of {}",username,time,event.getException().getMessage());
		
		userAccessLogRepo.save(new UserAccessLog(username, Type.Error , time, event.getException().getMessage()));
		
	}
	
	
	@EventListener
	@Transactional
	void onSessionDenied(HttpSessionDestroyedEvent event) {
		event.getSecurityContexts().stream()
		.findAny().ifPresent(auth->{
			var username = auth.getAuthentication().getName();
			var time = LocalDateTime.ofInstant(new Date(event.getTimestamp()).toInstant(), ZoneId.systemDefault());
			logger.info("{} is sign out in at {}",username,time);
			
			userAccessLogRepo.save(new UserAccessLog(username, Type.Signout, time));
		});
	}

}
