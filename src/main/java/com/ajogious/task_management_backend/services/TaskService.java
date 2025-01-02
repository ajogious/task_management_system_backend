package com.ajogious.task_management_backend.services;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ajogious.task_management_backend.dtos.TaskDTO;
import com.ajogious.task_management_backend.entities.Task;
import com.ajogious.task_management_backend.entities.User;
import com.ajogious.task_management_backend.repositories.TaskRepository;
import com.ajogious.task_management_backend.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional // Ensures atomic operations
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    // Add a new task
    public TaskDTO addTask(TaskDTO taskDTO) {
        User user = fetchUserById(taskDTO.getUserId());

        Task task = new Task();
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(convertToStatus(taskDTO.getStatus()));
        task.setUser(user);

        Task savedTask = taskRepository.save(task);
        return mapToDTO(savedTask);
    }

    // Fetch paginated tasks with filtering
    public Page<TaskDTO> getTasks(String status, String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Task.Status taskStatus = convertToStatus(status);

        Page<Task> tasks = taskRepository.searchTasks(taskStatus, search != null ? search : "", pageable);
        return tasks.map(this::mapToDTO);
    }

    // Fetch paginated tasks for a specific user with filtering
    public Page<TaskDTO> getUserTasks(Long userId, String status, String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Task.Status taskStatus = convertToStatus(status);

        Page<Task> tasks = taskRepository.searchUserTasks(userId, taskStatus, search != null ? search : "", pageable);
        return tasks.map(this::mapToDTO);
    }

    // Update a user's task
    public TaskDTO updateUserTask(Long userId, Long taskId, TaskDTO taskDTO) {
        Task task = validateUserOwnership(userId, taskId);

        if (taskDTO.getTitle() != null) {
            task.setTitle(taskDTO.getTitle());
        }
        if (taskDTO.getDescription() != null) {
            task.setDescription(taskDTO.getDescription());
        }
        if (taskDTO.getStatus() != null) {
            task.setStatus(convertToStatus(taskDTO.getStatus()));
        }

        task.setUpdated(LocalDateTime.now());
        Task updatedTask = taskRepository.save(task);

        return mapToDTO(updatedTask);
    }

    // Delete a user's task
    public void deleteUserTask(Long userId, Long taskId) {
        Task task = validateUserOwnership(userId, taskId);
        taskRepository.delete(task);
    }

    // --- Utility Methods --- //

    // Fetch user by ID with error handling
    private User fetchUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
    }

    // Validate task ownership
    private Task validateUserOwnership(Long userId, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));

        if (!task.getUser().getId().equals(userId)) {
            throw new SecurityException("Task ID " + taskId + " does not belong to User ID " + userId);
        }
        return task;
    }

    // Map entity to DTO
    private TaskDTO mapToDTO(Task task) {
        return new TaskDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus().name(),
                task.getCreated(),
                task.getUpdated(),
                task.getUser().getId());
    }

    // Convert status string to enum
    private Task.Status convertToStatus(String status) {
        if (status == null || status.equalsIgnoreCase("ALL")) {
            return null; // No filtering by status
        }

        try {
            return Task.Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + status);
        }
    }
}
