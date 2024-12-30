package com.ajogious.task_management_backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ajogious.task_management_backend.dtos.TaskStats;
import com.ajogious.task_management_backend.services.DashboardService;

import lombok.RequiredArgsConstructor;

@CrossOrigin
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats/{userId}")
    public ResponseEntity<TaskStats> getTaskStats(@PathVariable Long userId) {
        TaskStats stats = dashboardService.getTaskStats(userId);
        return ResponseEntity.ok(stats);
    }
}
