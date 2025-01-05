package com.ajogious.task_management_backend.controllers;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import com.ajogious.task_management_backend.dtos.TaskDTO;
import com.ajogious.task_management_backend.entities.User;
import com.ajogious.task_management_backend.repositories.AuthRepository;
import com.ajogious.task_management_backend.services.TaskService;

import lombok.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final AuthRepository authRepository;

    // Create Task Endpoint
    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody TaskDTO taskDTO) {
        try {
            TaskDTO savedTask = taskService.addTask(taskDTO);
            return buildSuccessResponse(savedTask, HttpStatus.CREATED);
        } catch (Exception e) {
            return handleException(e, "Failed to create task");
        }
    }

    // Get All Tasks Endpoint
    @GetMapping
    public ResponseEntity<?> getTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "") String search,
            PagedResourcesAssembler<TaskDTO> assembler) {

        try {
            Page<TaskDTO> tasks = taskService.getTasks(status, search, page, size);
            return ResponseEntity.ok(assembler.toModel(tasks));
        } catch (Exception e) {
            return handleException(e, "Failed to fetch tasks");
        }
    }

    // Get User Tasks Endpoint
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "") String search,
            PagedResourcesAssembler<TaskDTO> assembler,
            @PathVariable Long userId) {

        try {
            validateUserAccess(userId); // Authorization check
            Page<TaskDTO> tasks = taskService.getUserTasks(userId, status, search, page, size);
            return ResponseEntity.ok(assembler.toModel(tasks));
        } catch (Exception e) {
            return handleException(e, "Failed to fetch user tasks");
        }
    }

    // Update User Task Endpoint
    @PutMapping("/user/{userId}/tasks/{taskId}")
    public ResponseEntity<?> updateUserTask(
            @PathVariable Long userId,
            @PathVariable Long taskId,
            @RequestBody TaskDTO taskDTO) {

        try {
            validateUserAccess(userId); // Authorization check
            TaskDTO updatedTask = taskService.updateUserTask(userId, taskId, taskDTO);
            return buildSuccessResponse(updatedTask, HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e, "Failed to update task");
        }
    }

    // Delete User Task Endpoint
    @DeleteMapping("/user/{taskId}")
    public ResponseEntity<?> deleteUserTask(@PathVariable Long taskId) {
        try {
            Long userId = getCurrentUserId(); // Extract user ID
            taskService.deleteUserTask(userId, taskId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return handleException(e, "Failed to delete task");
        }
    }

    // Utility Methods

    // Retrieve Current User ID
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = authRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user.getId();
    }

    // Validate User Access
    private void validateUserAccess(Long userId) {
        Long loggedInUserId = getCurrentUserId();
        if (!loggedInUserId.equals(userId)) {
            throw new SecurityException("Access denied");
        }
    }

    // Build Success Response
    private ResponseEntity<?> buildSuccessResponse(Object data, HttpStatus status) {
        return ResponseEntity.status(status).body(data);
    }

    // Handle Exceptions
    private ResponseEntity<?> handleException(Exception e, String message) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (e instanceof UsernameNotFoundException) {
            status = HttpStatus.UNAUTHORIZED;
        } else if (e instanceof SecurityException) {
            status = HttpStatus.FORBIDDEN;
        } else if (e instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(Map.of("error", message, "details", e.getMessage()));
    }

}
