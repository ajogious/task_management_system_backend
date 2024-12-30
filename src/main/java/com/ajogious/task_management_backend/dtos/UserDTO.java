package com.ajogious.task_management_backend.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String role;
    private String image;
    private String gender;
    private String address;
    private String phoneNo;
    private boolean isActive;
    private LocalDateTime created;
    private LocalDateTime updated;

}