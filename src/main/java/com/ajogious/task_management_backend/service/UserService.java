package com.ajogious.task_management_backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ajogious.task_management_backend.model.User;
import com.ajogious.task_management_backend.repo.UserRepo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

	private final UserRepo userRepo;
	
	private final PasswordEncoder passwordEncoder;  
	
	public User saveUser(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword())); 
		return userRepo.save(user);
	}

	public User findByEmail(String email) {
		return userRepo.findByEmail(email);
	}

	public User authenticate(String email, String password) {
	    User user = userRepo.findByEmail(email);
	    
	    if (user != null && passwordEncoder.matches(password, user.getPassword())) {
	        return user;
	    }
	    
	    return null; 
	}


}
