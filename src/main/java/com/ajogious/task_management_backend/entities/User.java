package com.ajogious.task_management_backend.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(updatable = false, nullable = false)
    private LocalDateTime created;

    @Column(updatable = true, nullable = true)
    private LocalDateTime updated;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true, unique = true)
    private String phoneNo;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private String gender;

    @Column(nullable = true)
    private String address;

    @Column(nullable = true)
    private String image;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @Column(nullable = false)
    private boolean isActive = true;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Task> tasks;

    @PrePersist
    public void prePersist() {
        this.created = LocalDateTime.now();
        if (role == null) {
            this.role = Role.USER;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updated = LocalDateTime.now();
    }
}
