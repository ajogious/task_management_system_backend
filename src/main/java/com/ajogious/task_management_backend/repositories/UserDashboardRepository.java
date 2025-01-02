package com.ajogious.task_management_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ajogious.task_management_backend.entities.Task;

@Repository
public interface UserDashboardRepository extends JpaRepository<Task, Long> {

    @Query("SELECT COUNT(t) FROM Task t WHERE t.user.id = :userId")
    int getTotalTasksByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.user.id = :userId AND t.status = 'COMPLETED'")
    int getCompletedTasksByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.user.id = :userId AND t.status = 'PENDING'")
    int getPendingTasksByUserId(@Param("userId") Long userId);

}
