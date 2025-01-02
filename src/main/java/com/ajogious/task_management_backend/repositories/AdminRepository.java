package com.ajogious.task_management_backend.repositories;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ajogious.task_management_backend.entities.Task;
import com.ajogious.task_management_backend.entities.User;

@Repository
public interface AdminRepository extends JpaRepository<Task, Long> {

        @Query("SELECT COUNT(t) FROM Task t WHERE t.status = :status")
        long countByStatus(@Param("status") Task.Status status);

        // Count users registered between specific dates
        @Query("SELECT COUNT(u) FROM User u WHERE u.created BETWEEN :start AND :end")
        long countUsersRegisteredBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

        // Count users by gender
        @Query("SELECT COUNT(u) FROM User u WHERE LOWER(u.gender) = LOWER(:gender)")
        long countUsersByGender(@Param("gender") String gender);

        @Query("SELECT u FROM User u WHERE " +
                        "(LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(u.phoneNo) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
                        "(:role IS NULL OR u.role = :role)")
        Page<User> findAllWithFilters(@Param("search") String search,
                        @Param("role") String role,
                        Pageable pageable);
}
