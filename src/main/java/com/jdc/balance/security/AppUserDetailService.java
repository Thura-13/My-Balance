package com.jdc.balance.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.jdc.balance.model.repo.UserRepo;

@Component
public class AppUserDetailService implements UserDetailsService{

	@Autowired
	private UserRepo userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		var result = userRepo.findOneByLoginId(username);
			 return result.map(user -> User.withUsername(username)
								.password(user.getPassword())
								.authorities(AuthorityUtils.createAuthorityList(user.getRole().name()))
								.disabled(!user.isActive())
								.accountExpired(!user.isActive())
						
						.build())
				.orElseThrow(()-> new UsernameNotFoundException("There is no user with loginId %d".formatted(username)));
	}

	

}
