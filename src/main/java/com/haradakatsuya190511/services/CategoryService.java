package com.haradakatsuya190511.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.haradakatsuya190511.dtos.AddCategoryRequestDto;
import com.haradakatsuya190511.entities.Category;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.enums.CategoryType;
import com.haradakatsuya190511.repositories.CategoryRepository;

@Service
public class CategoryService {
	
	@Autowired
	CategoryRepository categoryRepository;
	
	public List<Category> getIncomeCategories(User user) {
		return categoryRepository.findIncomeByUserOrDefault(user);
	}
	
	public List<Category> getExpenseCategories(User user) {
		return categoryRepository.findExpenseByUserOrDefault(user);
	}
	
	public List<Category> getParentCategories(User user) {
		return categoryRepository.findParentCategoriesByUserOrDefault(user);
	}
	
	public Category addCategory(User user, AddCategoryRequestDto request) {
		Long parentId = request.getParentId();
		Category parentCategory = categoryRepository.findById(parentId).orElse(null);
		String name = request.getName();
		CategoryType type = request.getType();
		String description = request.getDescription();
		Category category = new Category(user, parentCategory, name, type, description);
		return category;
	}
}
