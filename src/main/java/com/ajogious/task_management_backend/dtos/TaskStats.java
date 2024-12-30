package com.ajogious.task_management_backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskStats {
    private int totalTasks;
    private int completedTasks;
    private int pendingTasks;
}