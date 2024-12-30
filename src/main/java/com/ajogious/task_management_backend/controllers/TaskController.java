package com.ajogious.task_management_backend.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import com.ajogious.task_management_backend.dtos.TaskDTO;
import com.ajogious.task_management_backend.entities.User;
import com.ajogious.task_management_backend.repositories.UserRepository;
import com.ajogious.task_management_backend.services.TaskService;

import lombok.RequiredArgsConstructor;

@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestBody TaskDTO taskDTO) {
        TaskDTO savedTask = taskService.addTask(taskDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<TaskDTO>>> getTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "") String search,
            PagedResourcesAssembler<TaskDTO> assembler) {

        Page<TaskDTO> tasks = taskService.getTasks(status, search, page, size);
        return ResponseEntity.ok(assembler.toModel(tasks));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<PagedModel<EntityModel<TaskDTO>>> getUserTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "") String search,
            PagedResourcesAssembler<TaskDTO> assembler,
            @PathVariable Long userId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Extract email from token

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Long loggedInUserId = user.getId();
        if (!userId.equals(loggedInUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Page<TaskDTO> tasks = taskService.getUserTasks(userId, status, search, page, size);
        return ResponseEntity.ok(assembler.toModel(tasks));
    }

    @PutMapping("/user/{userId}/tasks/{taskId}")
    public ResponseEntity<TaskDTO> updateUserTask(
            @PathVariable Long userId,
            @PathVariable Long taskId,
            @RequestBody TaskDTO taskDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Extract email from token

        // Validate the user making the request
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Update the task via the service layer
        TaskDTO updatedTask = taskService.updateUserTask(userId, taskId, taskDTO);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/user/{taskId}")
    public ResponseEntity<Void> deleteUserTask(@PathVariable Long taskId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Extract email from token

        // Validate the user making the request
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Delete the task via the service layer
        taskService.deleteUserTask(user.getId(), taskId);

        return ResponseEntity.noContent().build();
    }

}
