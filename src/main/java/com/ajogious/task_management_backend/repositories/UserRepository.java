package com.ajogious.task_management_backend.repositories;

import java.time.LocalDateTime;

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

        @Modifying
        @Transactional
        @Query("UPDATE User u SET u.isActive = false WHERE u.id = :id")
        void suspendUserById(@Param("id") Long id);

        @Modifying
        @Transactional
        @Query("UPDATE User u SET u.isActive = true WHERE u.id = :id")
        void activateUserById(@Param("id") Long id);

}
