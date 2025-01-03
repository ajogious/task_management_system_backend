package com.ajogious.task_management_backend.controllers;

import java.io.IOException;
import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ajogious.task_management_backend.dtos.UserDTO;
import com.ajogious.task_management_backend.services.UserService;

import lombok.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // Get User Endpoint
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUser(@PathVariable Long userId) {
        try {
            UserDTO userDTO = userService.getUserDetailsDTO(userId);
            return buildSuccessResponse(userDTO, HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e, "Failed to fetch user");
        }
    }

    // Update User Profile Endpoint
    @PutMapping("/update-profile/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestPart("user") UserDTO userDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            userDTO.setId(id);
            userService.updateUser(userDTO, file);

            // Response with updated profile image path
            Map<String, String> response = Map.of(
                    "message", "User updated successfully",
                    "imagePath", userDTO.getImage());
            return buildSuccessResponse(response, HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e, "Failed to update user profile");
        }
    }

    // Utility Methods

    // Success Response Builder
    private ResponseEntity<?> buildSuccessResponse(Object data, HttpStatus status) {
        return ResponseEntity.status(status).body(data);
    }

    // Exception Handler
    private ResponseEntity<?> handleException(Exception e, String message) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if (e instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
        } else if (e instanceof IOException) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        } else if (e instanceof NoSuchElementException) {
            status = HttpStatus.NOT_FOUND;
        }

        Map<String, String> errorResponse = Map.of(
                "error", message,
                "details", e.getMessage());
        return ResponseEntity.status(status).body(errorResponse);
    }

}
