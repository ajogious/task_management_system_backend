package com.ajogious.task_management_backend.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ajogious.task_management_backend.dtos.AdminDashboardStatsDTO;
import com.ajogious.task_management_backend.entities.Task;
import com.ajogious.task_management_backend.repositories.TaskRepository;
import com.ajogious.task_management_backend.repositories.UserRepository;

@Service
public class AdminAnalyticsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    public AdminDashboardStatsDTO getDashboardStats() {
        long totalUsers = userRepository.count();

        // Queries for monthly users and gender-specific counts
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).toLocalDate().atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);

        long totalMonthlyUsers = userRepository.countUsersRegisteredBetween(startOfMonth, endOfMonth);

        long totalMaleUsers = userRepository.countUsersByGender("Male");
        long totalFemaleUsers = userRepository.countUsersByGender("Female");

        // Queries for tasks count
        long totalTasks = taskRepository.count();
        long completedTasks = taskRepository.countByStatus(Task.Status.COMPLETED);
        long pendingTasks = taskRepository.countByStatus(Task.Status.PENDING);

        // Return statistics
        return new AdminDashboardStatsDTO(totalUsers, totalMonthlyUsers, totalMaleUsers + totalFemaleUsers,
                totalMaleUsers, totalFemaleUsers, totalTasks, completedTasks, pendingTasks);
    }
}