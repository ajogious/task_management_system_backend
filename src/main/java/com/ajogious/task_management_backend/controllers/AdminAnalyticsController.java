package com.ajogious.task_management_backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ajogious.task_management_backend.dtos.AdminDashboardStatsDTO;
import com.ajogious.task_management_backend.services.AdminAnalyticsService;

@CrossOrigin
@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminAnalyticsController {

    @Autowired
    private AdminAnalyticsService adminAnalyticsService;

    @GetMapping("/stats")
    public ResponseEntity<AdminDashboardStatsDTO> getDashboardStats() {
        AdminDashboardStatsDTO stats = adminAnalyticsService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

}