package com.ajogious.task_management_backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ajogious.task_management_backend.model.User;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

	User findByEmail(String email);

}
