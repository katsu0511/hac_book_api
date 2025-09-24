package com.haradakatsuya190511.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.haradakatsuya190511.dtos.AddCategoryRequestDto;
import com.haradakatsuya190511.dtos.CategoryResponseDto;
import com.haradakatsuya190511.dtos.ModifyCategoryRequestDto;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.services.AuthService;
import com.haradakatsuya190511.services.CategoryService;

@RestController
public class CategoryController {
	
	@Autowired
	AuthService authService;
	
	@Autowired
	CategoryService categoryService;
	
	@GetMapping("/categories/{id}")
	public ResponseEntity<CategoryResponseDto> getCategory(Principal principal, @PathVariable("id") Long id) {
		User user = authService.getUser(principal);
		return ResponseEntity.ok(categoryService.getCategory(user, id));
	}
	
	@GetMapping("/categories")
	public ResponseEntity<Map<String, List<CategoryResponseDto>>> getCategories(Principal principal) {
		User user = authService.getUser(principal);
		return ResponseEntity.ok(
			Map.of(
				"income", categoryService.getIncomeCategories(user),
				"expense", categoryService.getExpenseCategories(user)
			)
		);
	}
	
	@GetMapping("/categories/parents")
	public ResponseEntity<List<CategoryResponseDto>> getParentCategories(Principal principal) {
		User user = authService.getUser(principal);
		return ResponseEntity.ok(categoryService.getParentCategories(user));
	}
	
	@PostMapping("/categories")
	public ResponseEntity<CategoryResponseDto> createCategory(Principal principal, @RequestBody AddCategoryRequestDto request) {
		User user = authService.getUser(principal);
		return ResponseEntity.ok(categoryService.createCategory(user, request));
	}
	
	@PutMapping("/categories/{id}")
	public ResponseEntity<CategoryResponseDto> updateCategory(Principal principal, @PathVariable Long id, @RequestBody ModifyCategoryRequestDto request) {
		User user = authService.getUser(principal);
		return ResponseEntity.ok(categoryService.updateCategory(user, id, request));
	}
}
