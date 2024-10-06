package com.ajogious.task_management_backend.controller;
import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ajogious.task_management_backend.dto.LoginRequest;
import com.ajogious.task_management_backend.dto.LoginResponse;
import com.ajogious.task_management_backend.model.User;
import com.ajogious.task_management_backend.service.UserService;

import lombok.AllArgsConstructor;

@CrossOrigin
@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class UserController {

	private final UserService userService;
	private final PasswordEncoder passwordEncoder;  // Inject PasswordEncoder here

	// Register User
	@PostMapping("/register")
	public String registerUser(@RequestBody User user) {
		if (userService.findByEmail(user.getEmail()) != null) {
			return "User already exists";
		}
		userService.saveUser(user);
		return "Registration successful";
	}

	// Login User
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
	    User authenticatedUser = userService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());

	    if (authenticatedUser != null) {
	        // Return user details and success message
	        return ResponseEntity.ok(new LoginResponse("Login successful", authenticatedUser.getUsername(), authenticatedUser.getId(), authenticatedUser.getCreatedAt()));
	    } else {
	        // Return error message
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
	    }
	}



}
