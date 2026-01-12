package com.haradakatsuya190511.controllers;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.haradakatsuya190511.dtos.category.CreateCategoryRequestDto;
import com.haradakatsuya190511.dtos.category.CategoryDetailResponseDto;
import com.haradakatsuya190511.dtos.category.CategoryForEditResponseDto;
import com.haradakatsuya190511.dtos.category.CategoryListResponseDto;
import com.haradakatsuya190511.dtos.category.CategoryResponseDto;
import com.haradakatsuya190511.dtos.category.UpdateCategoryRequestDto;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.services.AuthService;
import com.haradakatsuya190511.services.CategoryService;

import jakarta.validation.Valid;

@RestController
public class CategoryController {
	
	private final AuthService authService;
	private final CategoryService categoryService;
	
	public CategoryController(AuthService authService, CategoryService categoryService) {
		this.authService = authService;
		this.categoryService = categoryService;
	}
	
	@GetMapping("/categories")
	public ResponseEntity<CategoryListResponseDto> getCategories(Principal principal) {
		User user = authService.getUser(principal);
		return ResponseEntity.ok(new CategoryListResponseDto(categoryService.getExpenseCategories(user), categoryService.getIncomeCategories(user)));
	}
	
	@GetMapping("/parent-categories")
	public ResponseEntity<CategoryListResponseDto> getParentCategories(Principal principal) {
		User user = authService.getUser(principal);
		return ResponseEntity.ok(new CategoryListResponseDto(categoryService.getParentExpenseCategories(user), categoryService.getParentIncomeCategories(user)));
	}
	
	@GetMapping("/categories/{id}")
	public ResponseEntity<CategoryDetailResponseDto> getCategory(Principal principal, @PathVariable("id") Long id) {
		User user = authService.getUser(principal);
		return ResponseEntity.ok(categoryService.getCategoryDetail(user, id));
	}
	
	@GetMapping("/categories/{id}/edit")
	public ResponseEntity<CategoryForEditResponseDto> getCategoryForEdit(Principal principal, @PathVariable("id") Long id) {
		User user = authService.getUser(principal);
		CategoryListResponseDto categories = new CategoryListResponseDto(categoryService.getParentExpenseCategories(user), categoryService.getParentIncomeCategories(user));
		return ResponseEntity.ok(new CategoryForEditResponseDto(categoryService.getCategory(user, id), categories));
	}
	
	@PostMapping("/categories")
	public ResponseEntity<CategoryResponseDto> createCategory(Principal principal, @Valid @RequestBody CreateCategoryRequestDto request) {
		User user = authService.getUser(principal);
		return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(user, request));
	}
	
	@PutMapping("/categories/{id}")
	public ResponseEntity<CategoryResponseDto> updateCategory(Principal principal, @PathVariable("id") Long id, @Valid @RequestBody UpdateCategoryRequestDto request) {
		User user = authService.getUser(principal);
		return ResponseEntity.ok(categoryService.updateCategory(user, id, request));
	}
}
