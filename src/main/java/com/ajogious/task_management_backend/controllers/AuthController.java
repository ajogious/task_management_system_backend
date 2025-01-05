package com.ajogious.task_management_backend.controllers;

import com.ajogious.task_management_backend.dtos.UserDTO;
import com.ajogious.task_management_backend.entities.User;
import com.ajogious.task_management_backend.security.JWTUtil;
import com.ajogious.task_management_backend.services.AuthService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JWTUtil jwtUtil;

    // Registration Endpoint
    @PostMapping("/register")
    public ResponseEntity<?> registerNewUser(@RequestPart User user, @RequestPart MultipartFile file) {
        try {
            authService.saveUser(user, file);
            return buildSuccessResponse("Registration successful");
        } catch (Exception e) {
            return handleException(e, "Error during registration");
        }
    }

    // Login Endpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            String token = authService.userLogin(user);
            UserDTO userDTO = authService.getUserDetailsDTO(user.getEmail());

            Map<String, Object> response = Map.of(
                    "message", "Login successful",
                    "token", token,
                    "user", userDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return handleException(e, "Login failed");
        }
    }

    // Validating user on login endpoint
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            boolean isValid = jwtUtil.validateToken(jwt, null);

            return buildSuccessResponse(Map.of("valid", isValid));
        } catch (Exception e) {
            return handleException(e, "Invalid token");
        }
    }

    // Logout endpoint
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            jwtUtil.invalidateToken(jwt);
            return buildSuccessResponse("Logout successful.");
        } catch (Exception e) {
            return handleException(e, "Logout failed");
        }
    }

    // Forgot password endpoint
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> requestBody) {
        try {
            authService.initiatePasswordReset(requestBody.get("email"));
            return buildSuccessResponse("Password reset link sent to your email.");
        } catch (Exception e) {
            return handleException(e, "Error during password reset initiation");
        }
    }

    // Reset password endpoint
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> requestBody) {
        try {
            authService.resetPassword(
                    requestBody.get("token"),
                    requestBody.get("newPassword"));
            return buildSuccessResponse("Password reset successful.");
        } catch (Exception e) {
            return handleException(e, "Error during password reset");
        }
    }

    // Utility Methods
    private ResponseEntity<Map<String, String>> buildSuccessResponse(String message) {
        return ResponseEntity.ok(Map.of("message", message));
    }

    private ResponseEntity<Map<String, Object>> buildSuccessResponse(Map<String, Object> body) {
        return ResponseEntity.ok(body);
    }

    private ResponseEntity<?> handleException(Exception e, String defaultMessage) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = e.getMessage();

        if (e instanceof UsernameNotFoundException || e instanceof BadCredentialsException) {
            status = HttpStatus.UNAUTHORIZED;
        } else if (e instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
        } else if (e instanceof IOException) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "I/O error: " + message;
        }

        return ResponseEntity.status(status).body(Map.of("message", defaultMessage + ": " + message));
    }
}
