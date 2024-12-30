package com.ajogious.task_management_backend.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ajogious.task_management_backend.entities.User;

import jakarta.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findUserWithTasks(Long id);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isActive = false WHERE u.id = :id")
    void suspendUserById(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isActive = true WHERE u.id = :id")
    void activateUserById(@Param("id") Long id);

    // Count users registered between specific dates
    @Query("SELECT COUNT(u) FROM User u WHERE u.created BETWEEN :start AND :end")
    long countUsersRegisteredBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Count users by gender
    @Query("SELECT COUNT(u) FROM User u WHERE LOWER(u.gender) = LOWER(:gender)")
    long countUsersByGender(@Param("gender") String gender);

    // Default methods for validation
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhoneNo(String phoneNo);

    // Find user by email
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.tasks WHERE LOWER(TRIM(u.email)) = LOWER(TRIM(:email))")
    User findUserByEmail(@Param("email") String email);

    User findUserByUsername(String username);

    @Query("SELECT u FROM User u WHERE " +
            "(LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "(:role IS NULL OR u.role = :role)")
    Page<User> findAllWithFilters(@Param("search") String search,
            @Param("role") String role,
            Pageable pageable);
}
