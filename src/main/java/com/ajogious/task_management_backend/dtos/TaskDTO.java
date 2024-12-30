package com.ajogious.task_management_backend.dtos;

import java.time.LocalDateTime;

import com.ajogious.task_management_backend.entities.Task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private String status;
    private LocalDateTime created;
    private LocalDateTime updated;
    private Long userId;

    // Constructor that accepts a Task object
    public TaskDTO(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.status = task.getStatus().name();
        this.created = task.getCreated();
        this.updated = task.getUpdated();
        this.userId = task.getUser().getId();
    }
}
