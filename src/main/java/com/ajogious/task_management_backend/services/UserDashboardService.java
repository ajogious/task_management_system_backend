package com.ajogious.task_management_backend.services;

import org.springframework.stereotype.Service;

import com.ajogious.task_management_backend.dtos.TaskStats;
import com.ajogious.task_management_backend.repositories.UserDashboardRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserDashboardService {

    private final UserDashboardRepository userDashboardRepository;

    public TaskStats getTaskStats(Long userId) {

        int totalTasks = userDashboardRepository.getTotalTasksByUserId(userId);
        int completedTasks = userDashboardRepository.getCompletedTasksByUserId(userId);
        int pendingTasks = userDashboardRepository.getPendingTasksByUserId(userId);

        return new TaskStats(totalTasks, completedTasks, pendingTasks);
    }
}
