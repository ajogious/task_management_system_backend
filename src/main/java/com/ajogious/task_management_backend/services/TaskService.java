package com.ajogious.task_management_backend.services;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ajogious.task_management_backend.dtos.TaskDTO;
import com.ajogious.task_management_backend.entities.Task;
import com.ajogious.task_management_backend.entities.User;
import com.ajogious.task_management_backend.repositories.TaskRepository;
import com.ajogious.task_management_backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskDTO addTask(TaskDTO taskDTO) {
        // Retrieve the existing user by ID
        User user = userRepository.findById(taskDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + taskDTO.getUserId()));

        // Create a new task and set the user relationship
        Task task = new Task();
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(Task.Status.valueOf(taskDTO.getStatus())); // Assuming Status is an enum
        task.setUser(user); // Associate task with existing user

        // Save the task
        Task savedTask = taskRepository.save(task);

        // Map back to DTO
        return mapToDTO(savedTask);
    }

    private TaskDTO mapToDTO(Task task) {
        return new TaskDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus().name(),
                task.getCreated(),
                task.getUpdated(),
                task.getUser().getId() // Return only the user ID
        );
    }

    public Page<TaskDTO> getTasks(String status, String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // Convert status string to enum or set it as null
        Task.Status taskStatus = (status != null && !status.equalsIgnoreCase("ALL"))
                ? Task.Status.valueOf(status.toUpperCase())
                : null;

        // Use empty string if search is null
        String searchQuery = (search != null) ? search : "";

        // Call the repository with updated search logic
        Page<Task> tasks = taskRepository.searchTasks(taskStatus, searchQuery, pageable);

        // Map tasks to DTOs
        return tasks.map(this::mapToDTO);
    }

    public Page<TaskDTO> getUserTasks(Long userId, String status, String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // Convert status string to enum or set it as null
        Task.Status taskStatus = (status != null && !status.equalsIgnoreCase("ALL"))
                ? Task.Status.valueOf(status.toUpperCase())
                : null;

        // Use empty string if search is null
        String searchQuery = (search != null) ? search : "";

        // Fetch tasks for the user
        Page<Task> tasks = taskRepository.searchUserTasks(userId, taskStatus, searchQuery, pageable);

        // Map tasks to DTOs
        return tasks.map(this::mapToDTO);
    }

    public TaskDTO updateUserTask(Long userId, Long taskId, TaskDTO taskDTO) {
        // Find the task
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));

        // Check if the task belongs to the user
        if (!task.getUser().getId().equals(userId)) {
            throw new SecurityException("Task does not belong to the user");
        }

        // Update the task fields
        if (taskDTO.getTitle() != null) {
            task.setTitle(taskDTO.getTitle());
        }
        if (taskDTO.getDescription() != null) {
            task.setDescription(taskDTO.getDescription());
        }
        if (taskDTO.getStatus() != null) {
            task.setStatus(Task.Status.valueOf(taskDTO.getStatus().toUpperCase())); // Convert string to enum
        }
        task.setUpdated(LocalDateTime.now());

        // Save the updated task
        Task updatedTask = taskRepository.save(task);

        // Map back to DTO
        return mapToDTO(updatedTask);
    }

    public void deleteUserTask(Long userId, Long taskId) {
        // Find the task
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));

        // Check if the task belongs to the user
        if (!task.getUser().getId().equals(userId)) {
            throw new SecurityException("Task does not belong to the user");
        }

        // Delete the task
        taskRepository.delete(task);
    }

}
