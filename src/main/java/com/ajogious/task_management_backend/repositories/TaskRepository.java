package com.ajogious.task_management_backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ajogious.task_management_backend.entities.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

        @Query("SELECT t FROM Task t WHERE " +
                        "(:status IS NULL OR t.status = :status) AND (" +
                        "LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(t.status) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "CAST(t.created AS string) LIKE CONCAT('%', :search, '%') OR " +
                        "CAST(t.updated AS string) LIKE CONCAT('%', :search, '%'))")
        Page<Task> searchTasks(
                        @Param("status") Task.Status status,
                        @Param("search") String search,
                        Pageable pageable);

        @Query("SELECT t FROM Task t WHERE t.user.id = :userId " +
                        "AND (:status IS NULL OR t.status = :status) " +
                        "AND (" +
                        "LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
                        "OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')) " +
                        "OR CAST(t.created AS string) LIKE CONCAT('%', :search, '%') " +
                        "OR CAST(t.updated AS string) LIKE CONCAT('%', :search, '%') " +
                        "OR LOWER(t.status) LIKE LOWER(CONCAT('%', :search, '%'))" +
                        ")")
        Page<Task> searchUserTasks(
                        @Param("userId") Long userId,
                        @Param("status") Task.Status status,
                        @Param("search") String search,
                        Pageable pageable);

}
