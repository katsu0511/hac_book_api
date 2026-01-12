package com.haradakatsuya190511.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.haradakatsuya190511.dtos.category.CreateCategoryRequestDto;
import com.haradakatsuya190511.dtos.category.CategoryDetailResponseDto;
import com.haradakatsuya190511.dtos.category.CategoryResponseDto;
import com.haradakatsuya190511.dtos.category.UpdateCategoryRequestDto;
import com.haradakatsuya190511.dtos.category.shared.CategoryRequest;
import com.haradakatsuya190511.entities.Category;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.exceptions.CategoryNotFoundException;
import com.haradakatsuya190511.exceptions.InvalidParentCategoryException;
import com.haradakatsuya190511.repositories.CategoryRepository;

@Service
public class CategoryService {
	
	private final CategoryRepository categoryRepository;
	
	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}
	
	public List<CategoryResponseDto> getDefaultExpenseCategories() {
		return categoryRepository.findDefaultExpenseCategories().stream()
			.map(CategoryResponseDto::new)
			.toList();
	}
	
	public List<CategoryResponseDto> getDefaultIncomeCategories() {
		return categoryRepository.findDefaultIncomeCategories().stream()
			.map(CategoryResponseDto::new)
			.toList();
	}
	
	public List<CategoryResponseDto> getExpenseCategories(User user) {
		List<CategoryResponseDto> expenseParents = getParentExpenseCategories(user);
		List<CategoryResponseDto> expenseChildren = getChildExpenseCategories(user);
		return sortCategories(expenseParents, expenseChildren);
	}
	
	public List<CategoryResponseDto> getIncomeCategories(User user) {
		List<CategoryResponseDto> incomeParents = getParentIncomeCategories(user);
		List<CategoryResponseDto> incomeChildren = getChildIncomeCategories(user);
		return sortCategories(incomeParents, incomeChildren);
	}
	
	public List<CategoryResponseDto> getParentExpenseCategories(User user) {
		return sortParentCategories(categoryRepository.findParentExpenseCategories(user).stream()
			.map(CategoryResponseDto::new)
			.collect(Collectors.toCollection(ArrayList::new))
		);
	}
	
	public List<CategoryResponseDto> getParentIncomeCategories(User user) {
		return sortParentCategories(categoryRepository.findParentIncomeCategories(user).stream()
			.map(CategoryResponseDto::new)
			.collect(Collectors.toCollection(ArrayList::new))
		);
	}
	
	public CategoryResponseDto getCategory(User user, Long id) {
		return categoryRepository.findWithParentById(id)
			.filter(c -> c.getUser() != null && c.getUser().getId().equals(user.getId()))
			.map(CategoryResponseDto::new)
			.orElseThrow(CategoryNotFoundException::new);
	}
	
	public CategoryDetailResponseDto getCategoryDetail(User user, Long id) {
		CategoryResponseDto category = getCategory(user, id);
		String parentName = null;
		if (category.getParentId() != null) {
			parentName = getCategoryName(category.getParentId());
		}
		return new CategoryDetailResponseDto(category, parentName);
	}
	
	public CategoryResponseDto createCategory(User user, CreateCategoryRequestDto request) {
		Category category = new Category(user);
		applyCategoryInfo(category, user, request);
		return new CategoryResponseDto(categoryRepository.save(category));
	}
	
	public CategoryResponseDto updateCategory(User user, Long id, UpdateCategoryRequestDto request) {
		Category category = categoryRepository.findById(id)
				.filter(c -> c.getUser().getId().equals(user.getId()))
				.orElseThrow(CategoryNotFoundException::new);
		applyCategoryInfo(category, user, request);
		return new CategoryResponseDto(categoryRepository.save(category));
	}
	
	private String getCategoryName(Long id) {
		return categoryRepository.findById(id)
			.map(Category::getName)
			.orElseThrow(CategoryNotFoundException::new);
	}
	
	private List<CategoryResponseDto> getChildExpenseCategories(User user) {
		return categoryRepository.findChildExpenseCategories(user).stream()
			.map(CategoryResponseDto::new)
			.collect(Collectors.toCollection(ArrayList::new));
	}
	
	private List<CategoryResponseDto> getChildIncomeCategories(User user) {
		return categoryRepository.findChildIncomeCategories(user).stream()
			.map(CategoryResponseDto::new)
			.collect(Collectors.toCollection(ArrayList::new));
	}
	
	private List<CategoryResponseDto> sortParentCategories(List<CategoryResponseDto> list) {
		final String OTHERS = "Others";
		return Stream.concat(
			list.stream().filter(c -> !c.getName().equals(OTHERS)),
			list.stream().filter(c -> c.getName().equals(OTHERS))
		).toList();
	}
	
	private List<CategoryResponseDto> sortCategories(List<CategoryResponseDto> parents, List<CategoryResponseDto> children) {
		Map<Long, List<CategoryResponseDto>> parentToChildrenMap = children.stream()
			.collect(Collectors.groupingBy(CategoryResponseDto::getParentId));
		
		List<CategoryResponseDto> sortedCategories = new ArrayList<>();
		for (CategoryResponseDto p : parents) {
			sortedCategories.add(p);
			List<CategoryResponseDto> childrenForParent = parentToChildrenMap.getOrDefault(p.getId(), Collections.emptyList());
			sortedCategories.addAll(childrenForParent);
		}
		return sortedCategories;
	}
	
	private void applyCategoryInfo(Category category, User user, CategoryRequest request) {
		Long parentId = request.getParentId();
		Category parent = parentId == null ? null : categoryRepository.findById(parentId).orElseThrow(CategoryNotFoundException::new);
		if (parent != null) {
			boolean isDefault = parent.getUser() == null;
			boolean isUserOwned = parent.getUser() != null && parent.getUser().getId().equals(user.getId());
			boolean isTopLevel = parent.getParentCategory() == null;
			if (!(isDefault || (isUserOwned && isTopLevel))) throw new InvalidParentCategoryException();
		}
		category.setParentCategory(parent);
		category.setName(request.getName());
		category.setType(request.getType());
		category.setDescription(request.getDescription());
	}
}
