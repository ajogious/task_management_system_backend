package com.ajogious.task_management_backend.controllers;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ajogious.task_management_backend.dtos.*;
import com.ajogious.task_management_backend.services.*;

import lombok.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;

    // Admin Analytic endpoint to get all statistics
    @GetMapping("/dashboard/stats")
    public ResponseEntity<AdminDashboardStatsDTO> getDashboardStats() {
        return buildOkResponse(adminService.getDashboardStats());
    }

    // Get all users endpoint by the admin with search, filters, and pagination
    @GetMapping("/users")
    public ResponseEntity<Page<UserDTO>> getAllUsersInfo(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        return buildOkResponse(adminService.getAllUsersInfo(search, role, page, size));
    }

    // Delete a user's endpoint by the admin
    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return buildNoContentResponse();
    }

    // Suspend or Activate a user dynamically based on action type
    @PutMapping("/{id}/{action}")
    public ResponseEntity<Void> updateUserStatus(@PathVariable Long id, @PathVariable String action) {
        switch (action.toLowerCase()) {
            case "suspend" -> userService.suspendUser(id);
            case "activate" -> userService.activateUser(id);
            default -> throw new IllegalArgumentException("Invalid action: " + action);
        }
        return buildNoContentResponse();
    }

    // Utility methods for ResponseEntity handling
    private <T> ResponseEntity<T> buildOkResponse(T body) {
        return ResponseEntity.ok(body);
    }

    private ResponseEntity<Void> buildNoContentResponse() {
        return ResponseEntity.noContent().build();
    }
}
