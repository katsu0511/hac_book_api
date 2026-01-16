package com.haradakatsuya190511.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.haradakatsuya190511.services.CategoryService;

import jakarta.validation.Valid;

@RestController
public class CategoryController {
	
	private final CategoryService categoryService;
	
	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	
	@GetMapping("/categories")
	public ResponseEntity<CategoryListResponseDto> getCategories(@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(new CategoryListResponseDto(categoryService.getExpenseCategories(user), categoryService.getIncomeCategories(user)));
	}
	
	@GetMapping("/parent-categories")
	public ResponseEntity<CategoryListResponseDto> getParentCategories(@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(new CategoryListResponseDto(categoryService.getParentExpenseCategoriesWithoutOthers(user), categoryService.getParentIncomeCategoriesWithoutOthers(user)));
	}
	
	@GetMapping("/categories/{id}")
	public ResponseEntity<CategoryDetailResponseDto> getCategory(@AuthenticationPrincipal User user, @PathVariable("id") Long id) {
		return ResponseEntity.ok(categoryService.getCategoryDetail(user, id));
	}
	
	@GetMapping("/categories/{id}/edit")
	public ResponseEntity<CategoryForEditResponseDto> getCategoryForEdit(@AuthenticationPrincipal User user, @PathVariable("id") Long id) {
		CategoryListResponseDto categories = new CategoryListResponseDto(categoryService.getParentExpenseCategoriesWithoutOthers(user), categoryService.getParentIncomeCategoriesWithoutOthers(user));
		return ResponseEntity.ok(new CategoryForEditResponseDto(categoryService.getCategory(user, id), categories));
	}
	
	@PostMapping("/categories")
	public ResponseEntity<CategoryResponseDto> createCategory(@AuthenticationPrincipal User user, @Valid @RequestBody CreateCategoryRequestDto request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(user, request));
	}
	
	@PutMapping("/categories/{id}")
	public ResponseEntity<CategoryResponseDto> updateCategory(@AuthenticationPrincipal User user, @PathVariable("id") Long id, @Valid @RequestBody UpdateCategoryRequestDto request) {
		return ResponseEntity.ok(categoryService.updateCategory(user, id, request));
	}
}
