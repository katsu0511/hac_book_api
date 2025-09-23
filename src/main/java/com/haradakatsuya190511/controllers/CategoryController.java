package com.haradakatsuya190511.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.haradakatsuya190511.dtos.AddCategoryRequestDto;
import com.haradakatsuya190511.dtos.CategoryResponseDto;
import com.haradakatsuya190511.entities.Category;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.services.AuthService;
import com.haradakatsuya190511.services.CategoryService;

@RestController
public class CategoryController {
	
	@Autowired
	AuthService authService;
	
	@Autowired
	CategoryService categoryService;
	
	@GetMapping("/display/category")
	public ResponseEntity<Map<String, List<CategoryResponseDto>>> displayCategory(Principal principal) {
		User user = authService.getUser(principal);
		return ResponseEntity.ok(
			Map.of(
				"income", categoryService.getIncomeCategories(user),
				"expense", categoryService.getExpenseCategories(user)
			)
		);
	}
	
	@GetMapping("/parentCategory")
	public ResponseEntity<List<Category>> parentCategory(Principal principal) {
		User user = authService.getUser(principal);
		return ResponseEntity.ok(categoryService.getParentCategories(user));
	}
	
	@PostMapping("/add/category")
	public ResponseEntity<Map<String, Category>> addCategory(@RequestBody AddCategoryRequestDto request, Principal principal) {
		User user = authService.getUser(principal);
		Category category = categoryService.addCategory(user, request);
		return ResponseEntity.ok(Map.of("category", category));
	}
}
