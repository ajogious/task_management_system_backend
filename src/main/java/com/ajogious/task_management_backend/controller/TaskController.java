package com.ajogious.task_management_backend.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ajogious.task_management_backend.model.Item;
import com.ajogious.task_management_backend.service.ItemService;

@CrossOrigin
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

	@Autowired
	private ItemService itemService;

	@GetMapping("/{userId}")
	public List<Item> getItemsByUser(@PathVariable Long userId) {
		return itemService.getItemsByUser(userId);
	}

	@GetMapping("/task/{id}")
	public ResponseEntity<Item> getTaskById(@PathVariable Long id) {
		Item item = itemService.getItemById(id);
		if (item != null) {
			return new ResponseEntity<>(item, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("/add")
	public Item addItem(@RequestBody Item item) {
		return itemService.addItem(item);
	}

	@PutMapping("/update/{id}")
	public Item updateItem(@PathVariable Long id, @RequestBody Item item) {
	    item.setId(id);
	    item.setUpdatedAt(LocalDateTime.now());
	    return itemService.updateItem(item);
	}


	@DeleteMapping("/delete/{id}")
	public void deleteItem(@PathVariable Long id) {
		itemService.deleteItem(id);
	}

}
