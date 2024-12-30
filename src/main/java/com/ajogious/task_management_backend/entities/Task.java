package com.ajogious.task_management_backend.entities;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(updatable = false, nullable = false)
    private LocalDateTime created;

    @Column(updatable = true, nullable = true)
    private LocalDateTime updated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @PrePersist
    public void prePersist() {
        this.created = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updated = LocalDateTime.now();
    }

    public enum Status {
        PENDING, COMPLETED
    }
}
