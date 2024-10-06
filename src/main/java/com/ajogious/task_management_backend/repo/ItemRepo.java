package com.ajogious.task_management_backend.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ajogious.task_management_backend.model.Item;

@Repository
public interface ItemRepo extends JpaRepository<Item, Long>{
	List<Item> findByUserId(Long userId);
}
