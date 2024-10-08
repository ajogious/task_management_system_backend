package com.ajogious.task_management_backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

	private String message;
	private String username;
	private Long userId;
	private String avatar_url;
	private LocalDateTime createdAt;

}
