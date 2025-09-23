package com.haradakatsuya190511.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.haradakatsuya190511.dtos.AddCategoryRequestDto;
import com.haradakatsuya190511.dtos.CategoryResponseDto;
import com.haradakatsuya190511.dtos.shared.CategoryRequest;
import com.haradakatsuya190511.entities.Category;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.exceptions.CategoryNotFoundException;
import com.haradakatsuya190511.repositories.CategoryRepository;

@Service
public class CategoryService {
	
	@Autowired
	CategoryRepository categoryRepository;
	
	public List<CategoryResponseDto> getIncomeCategories(User user) {
		return categoryRepository.findIncomeByUserOrDefault(user).stream()
			.map(CategoryResponseDto::new)
			.toList();
	}
	
	public List<CategoryResponseDto> getExpenseCategories(User user) {
		return categoryRepository.findExpenseByUserOrDefault(user).stream()
			.map(CategoryResponseDto::new)
			.toList();
	}
	
	public List<Category> getParentCategories(User user) {
		return categoryRepository.findParentCategoriesByUserOrDefault(user);
	}
	
	public CategoryResponseDto getCategory(User user, Long id) {
		return categoryRepository.findById(id)
			.filter(c -> c.getUser().getId().equals(user.getId()))
			.map(CategoryResponseDto::new)
			.orElseThrow(CategoryNotFoundException::new);
	}
	
	public Category addCategory(User user, AddCategoryRequestDto request) {
		Category category = new Category(user);
		applyCategoryInfo(category, request);
		return categoryRepository.save(category);
	}
	
	private void applyCategoryInfo(Category category, CategoryRequest request) {
		Long parentId = request.getParentId();
		Category parentCategory = categoryRepository.findById(parentId).orElse(null);
		category.setParentCategory(parentCategory);
		category.setName(request.getName());
		category.setType(request.getType());
		category.setDescription(request.getDescription());
	}
}
