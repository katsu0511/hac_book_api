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
import com.haradakatsuya190511.enums.CategoryType;
import com.haradakatsuya190511.exceptions.CategoryNotFoundException;
import com.haradakatsuya190511.exceptions.InvalidCategoryException;
import com.haradakatsuya190511.repositories.CategoryRepository;

@Service
public class CategoryService {
	
	private static final String OTHERS = "Others";
	private final CategoryRepository categoryRepository;
	
	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}
	
	public List<CategoryResponseDto> getExpenseCategories(User user) {
		return getCategoriesByCategoryType(user, CategoryType.EXPENSE);
	}
	
	public List<CategoryResponseDto> getIncomeCategories(User user) {
		return getCategoriesByCategoryType(user, CategoryType.INCOME);
	}
	
	public List<CategoryResponseDto> getParentExpenseCategoriesWithoutOthers(User user) {
		return getParentCategoriesByCategoryType(user, CategoryType.EXPENSE).stream()
			.filter(c -> !c.getName().equals(OTHERS))
			.toList();
	}
	
	public List<CategoryResponseDto> getParentIncomeCategoriesWithoutOthers(User user) {
		return getParentCategoriesByCategoryType(user, CategoryType.INCOME).stream()
			.filter(c -> !c.getName().equals(OTHERS))
			.toList();
	}
	
	public CategoryResponseDto getCategory(User user, Long id) {
		return categoryRepository.findWithParentByIdAndUserId(id, user.getId())
			.filter(c -> c.getUser().getId().equals(user.getId()))
			.map(CategoryResponseDto::new)
			.orElseThrow(CategoryNotFoundException::new);
	}
	
	public CategoryDetailResponseDto getCategoryDetail(User user, Long id) {
		CategoryResponseDto category = getCategory(user, id);
		String parentName = null;
		if (category.getParentId() != null) {
			parentName = getCategoryName(user, category.getParentId());
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
	
	private String getCategoryName(User user, Long id) {
		return categoryRepository.findById(id)
			.filter(c -> c.getUser().getId().equals(user.getId()))
			.map(Category::getName)
			.orElseThrow(CategoryNotFoundException::new);
	}
	
	private List<CategoryResponseDto> getCategoriesByCategoryType(User user, CategoryType type) {
		List<CategoryResponseDto> parents = getParentCategoriesByCategoryType(user, type);
		List<CategoryResponseDto> children = getChildCategoriesByCategoryType(user, type);
		return sortCategories(parents, children);
	}
	
	private List<CategoryResponseDto> getParentCategoriesByCategoryType(User user, CategoryType type) {
		return sortParentCategories(categoryRepository.findParentCategories(user, type).stream()
			.map(CategoryResponseDto::new)
			.collect(Collectors.toCollection(ArrayList::new))
		);
	}
	
	private List<CategoryResponseDto> getChildCategoriesByCategoryType(User user, CategoryType type) {
		return categoryRepository.findChildCategories(user, type).stream()
			.map(CategoryResponseDto::new)
			.collect(Collectors.toCollection(ArrayList::new));
	}
	
	private List<CategoryResponseDto> sortParentCategories(List<CategoryResponseDto> list) {
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
			boolean isUserOwned = parent.getUser().getId().equals(user.getId());
			boolean isTopLevel = parent.getParentCategory() == null;
			boolean isOthers = parent.getName().equals(OTHERS);
			if (!(isUserOwned && isTopLevel && !isOthers)) throw new InvalidCategoryException();
		}
		category.setParentCategory(parent);
		category.setName(request.getName());
		category.setType(request.getType());
		category.setDescription(request.getDescription());
	}
}
