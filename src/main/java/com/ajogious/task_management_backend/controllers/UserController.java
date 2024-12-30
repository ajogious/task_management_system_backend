package com.ajogious.task_management_backend.controllers;

import com.ajogious.task_management_backend.dtos.UserDTO;
import com.ajogious.task_management_backend.entities.User;
import com.ajogious.task_management_backend.security.JWTUtil;
import com.ajogious.task_management_backend.services.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.module.ResolutionException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;
    private final JWTUtil jwtUtil;

    // Registration Endpoint
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerNewUser(@RequestPart User user,
            @RequestPart MultipartFile file) {
        Map<String, String> response = new HashMap<>();

        try {
            userService.saveUser(user, file);
            response.put("message", "Registration successful");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (IOException e) {
            response.put("message", "Error saving image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            response.put("message", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Traditional Login Endpoint
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        try {
            String token = userService.userLogin(user);
            UserDTO userDTO = userService.getUserDetailsDTO(user.getEmail());

            response.put("message", "Login successful");
            response.put("token", token);
            response.put("user", userDTO);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (UsernameNotFoundException | BadCredentialsException ex) {
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            response.put("message", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");

            boolean isValid = jwtUtil.validateToken(jwt, null);

            if (isValid) {
                return ResponseEntity.ok(Collections.singletonMap("valid", true));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("valid", false));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token.");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");

            jwtUtil.invalidateToken(jwt);

            return ResponseEntity.ok(Collections.singletonMap("message", "Logout successful."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Logout failed: " + e.getMessage()));
        }
    }

    @GetMapping("user/{userId}")
    public ResponseEntity<?> getUser(@PathVariable Long userId) {
        try {
            UserDTO userDTO = userService.getUserDetailsDTO(userId);
            return ResponseEntity.status(HttpStatus.OK).body(userDTO);

        } catch (ResolutionException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/update-profile/{id}")
    public ResponseEntity<Map<String, String>> updateUser(
            @PathVariable Long id,
            @RequestPart("user") UserDTO userDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        try {
            userDTO.setId(id);
            userService.updateUser(userDTO, file);
            UserDTO existingUser = userService.getUserDetailsDTO(id);
            response.put("message", "User updated successfully");
            response.put("imagePath", existingUser.getImage());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (IOException e) {
            response.put("message", "Error saving image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            response.put("message", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Get all users with search, filters, and pagination
    @GetMapping("/users")
    public ResponseEntity<Page<UserDTO>> getAllUsersInfo(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Page<UserDTO> users = userService.getAllUsersInfo(search, role, page, size);
        return ResponseEntity.ok(users);
    }

    // Delete a user by ID
    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // Suspend a user by ID
    @PutMapping("/{id}/suspend")
    public ResponseEntity<Void> suspendUser(@PathVariable Long id) {
        userService.suspendUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.noContent().build();
    }

}
