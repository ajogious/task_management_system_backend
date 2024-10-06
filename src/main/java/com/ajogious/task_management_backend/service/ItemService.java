package com.ajogious.task_management_backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ajogious.task_management_backend.model.Item;
import com.ajogious.task_management_backend.model.User;
import com.ajogious.task_management_backend.repo.UserRepo;
import com.ajogious.task_management_backend.repo.ItemRepo;

@Service
public class ItemService {

	@Autowired
	private ItemRepo itemRepo;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder; // To compare hashed passwords

	public List<Item> getItemsByUser(Long userId) {
		return itemRepo.findByUserId(userId);
	}

	public Item getItemById(Long id) {
		return itemRepo.findById(id).orElse(null);
	}

	public Item addItem(Item item) {
		return itemRepo.save(item);
	}

	public Item updateItem(Item item) {
        item.setUpdatedAt(LocalDateTime.now()); // Set the updated date and time
        return itemRepo.save(item); // Save the updated item
    }

	public void deleteItem(Long id) {
		itemRepo.deleteById(id);
	}

	// Authentication method
	public User authenticate(String email, String password) {
		User user = userRepo.findByEmail(email);
		if (user != null && passwordEncoder.matches(password, user.getPassword())) {
			return user;
		}
		return null;
	}
}
