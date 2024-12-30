package com.ajogious.task_management_backend.dtos;

import lombok.Data;

@Data
public class AdminDashboardStatsDTO {

    private long totalUsers;
    private long totalMonthlyUsers;
    private long totalGenders;
    private long totalMaleUsers;
    private long totalFemaleUsers;
    private long totalUsersTasks;
    private long totalCompletedTasks;
    private long totalPendingTasks;

    public AdminDashboardStatsDTO(long totalUsers, long totalMonthlyUsers, long totalGenders,
            long totalMaleUsers, long totalFemaleUsers, long totalUsersTasks,
            long totalCompletedTasks, long totalPendingTasks) {
        this.totalUsers = totalUsers;
        this.totalMonthlyUsers = totalMonthlyUsers;
        this.totalGenders = totalGenders;
        this.totalMaleUsers = totalMaleUsers;
        this.totalFemaleUsers = totalFemaleUsers;
        this.totalUsersTasks = totalUsersTasks;
        this.totalCompletedTasks = totalCompletedTasks;
        this.totalPendingTasks = totalPendingTasks;
    }

}