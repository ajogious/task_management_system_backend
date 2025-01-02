package com.ajogious.task_management_backend.services;

import java.io.IOException;
import java.nio.file.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ajogious.task_management_backend.dtos.UserDTO;
import com.ajogious.task_management_backend.entities.User;
import com.ajogious.task_management_backend.repositories.AuthRepository;
import com.ajogious.task_management_backend.repositories.UserRepository;
import com.ajogious.task_management_backend.security.JWTUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authManager;
    private final AuthRepository authRepository;
    private final JWTUtil jwtUtil;

    @Value("${file.upload-dir}")
    private String uploadDir;

    // Register a new user
    public User saveUser(User user, MultipartFile imageFile) throws IOException {
        validateUserInputs(user); // Check duplicates

        user.setPassword(encodePassword(user.getPassword()));
        user.setImage(saveUserImage(user.getUsername(), imageFile));

        return userRepository.save(user);
    }

    // User Login
    public String userLogin(User user) {
        authenticateUser(user.getEmail(), user.getPassword());
        User savedUser = fetchUserByEmail(user.getEmail());
        return jwtUtil.generateToken(savedUser.getEmail());
    }

    // Password Reset - Initiate
    public void initiatePasswordReset(String email) {
        User user = fetchUserByEmail(email);
        String token = jwtUtil.generatePasswordResetToken(user);
        String resetLink = "http://localhost:5173/reset-password?token=" + token;
        emailService.sendPasswordResetEmail(email, resetLink);
    }

    // Password Reset - Complete
    public void resetPassword(String token, String newPassword) {
        String email = validateTokenAndExtractEmail(token);
        User user = fetchUserByEmail(email);

        user.setPassword(encodePassword(newPassword));
        userRepository.save(user);
    }

    // Get User DTO
    public UserDTO getUserDetailsDTO(String email) {
        return mapToUserDTO(fetchUserByEmail(email));
    }

    // --- Private Utility Methods --- //

    // Validate username, email, and phone number
    private void validateUserInputs(User user) {
        if (authRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (authRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (authRepository.existsByPhoneNo(user.getPhoneNo())) {
            throw new IllegalArgumentException("Phone number already exists");
        }
    }

    // Encode password
    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    // Authenticate user
    private void authenticateUser(String email, String password) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid email or password.");
        }
    }

    // Fetch user by email
    private User fetchUserByEmail(String email) {
        return authRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
    }

    // Validate token and extract email
    private String validateTokenAndExtractEmail(String token) {
        String email = jwtUtil.extractUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (!jwtUtil.validateToken(token, userDetails)) {
            throw new IllegalArgumentException("Invalid or expired token.");
        }
        return email;
    }

    // Save user profile image
    private String saveUserImage(String username, MultipartFile imageFile) throws IOException {
        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("Image file is required.");
        }

        String userFolder = Paths.get(uploadDir, "users", username).toString();
        Path uploadPath = Paths.get(userFolder);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = imageFile.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        // Handle file overwrite
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
        Files.copy(imageFile.getInputStream(), filePath);

        return Paths.get("users", username, fileName).toString(); // Return relative path
    }

    // Map User entity to DTO
    private UserDTO mapToUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name(),
                user.getImage(),
                user.getGender(),
                user.getAddress(),
                user.getPhoneNo(),
                user.isActive(),
                user.getUpdated(),
                user.getCreated());
    }
}
