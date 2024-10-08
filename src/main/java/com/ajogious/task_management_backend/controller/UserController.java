package com.ajogious.task_management_backend.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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

	// Directory to store uploaded files (ensure this path exists)
	private static final String UPLOADED_FOLDER = "uploads/";

	// Register User with Avatar
	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestParam("fullName") String fullName,
			@RequestParam("username") String username, @RequestParam("email") String email,
			@RequestParam("password") String password, @RequestParam("avatar") MultipartFile avatar) {
		if (userService.findByEmail(email) != null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists");
		}

		try {
			// Check if upload directory exists, if not create it
			Path uploadDir = Paths.get(UPLOADED_FOLDER);
			if (!Files.exists(uploadDir)) {
				Files.createDirectories(uploadDir);
			}

			// Save the avatar file to the server
			String avatarUrl = saveAvatarFile(avatar);

			// Create and save user with avatar URL
			User user = new User();
			user.setFullName(fullName);
			user.setUsername(username);
			user.setEmail(email);
			user.setPassword(password);
			user.setAvatarUrl(avatarUrl); // Set avatar URL

			userService.saveUser(user);
			return ResponseEntity.ok("Registration successful");

		} catch (IOException e) {
			System.err.println("Error while uploading avatar: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading avatar");
		}
	}

	// Upload Avatar and return the URL
	private String saveAvatarFile(MultipartFile avatar) throws IOException {
		if (avatar.isEmpty()) {
			throw new IOException("Avatar file is empty");
		}

		// Save avatar file
		byte[] bytes = avatar.getBytes();
		Path path = Paths.get(UPLOADED_FOLDER + avatar.getOriginalFilename());
		Files.write(path, bytes);

		// Return the URL of the avatar
		return "/uploads/" + avatar.getOriginalFilename();
	}

	// Login User
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
		User authenticatedUser = userService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());

		if (authenticatedUser != null) {
			// Return user details and success message
			return ResponseEntity.ok(new LoginResponse("Login successful", authenticatedUser.getUsername(),
					authenticatedUser.getId(), authenticatedUser.getAvatarUrl(), authenticatedUser.getCreatedAt()));
		} else {
			// Return error message for invalid credentials
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Collections.singletonMap("message", "Invalid email or password"));
		}
	}

}
