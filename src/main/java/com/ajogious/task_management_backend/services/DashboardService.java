package com.ajogious.task_management_backend.services;

import org.springframework.stereotype.Service;

import com.ajogious.task_management_backend.dtos.TaskStats;
import com.ajogious.task_management_backend.repositories.TaskRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DashboardService {

    private final TaskRepository taskRepository;

    public TaskStats getTaskStats(Long userId) {

        int totalTasks = taskRepository.getTotalTasksByUserId(userId);
        int completedTasks = taskRepository.getCompletedTasksByUserId(userId);
        int pendingTasks = taskRepository.getPendingTasksByUserId(userId);

        return new TaskStats(totalTasks, completedTasks, pendingTasks);
    }
}
