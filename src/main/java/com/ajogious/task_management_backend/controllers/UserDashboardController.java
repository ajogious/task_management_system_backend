package com.ajogious.task_management_backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ajogious.task_management_backend.dtos.TaskStats;
import com.ajogious.task_management_backend.services.UserDashboardService;

import lombok.*;

@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/dashboard")
public class UserDashboardController {

    private final UserDashboardService userDashboardService;

    // dashboard stats end point
    @GetMapping("/stats/{userId}")
    public ResponseEntity<TaskStats> getTaskStats(@PathVariable Long userId) {
        TaskStats stats = userDashboardService.getTaskStats(userId);
        return ResponseEntity.ok(stats);
    }
}
