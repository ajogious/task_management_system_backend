package com.ajogious.task_management_backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ajogious.task_management_backend.entities.User;

@Repository
public interface AuthRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // Default methods for validation
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhoneNo(String phoneNo);

    // Find user by email
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.tasks WHERE LOWER(TRIM(u.email)) = LOWER(TRIM(:email))")
    User findUserByEmail(@Param("email") String email);

}
