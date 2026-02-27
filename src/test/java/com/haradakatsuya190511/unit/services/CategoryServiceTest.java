package com.haradakatsuya190511.unit.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.haradakatsuya190511.dtos.category.CategoryDetailResponseDto;
import com.haradakatsuya190511.dtos.category.CategoryResponseDto;
import com.haradakatsuya190511.dtos.category.CreateCategoryRequestDto;
import com.haradakatsuya190511.dtos.category.UpdateCategoryRequestDto;
import com.haradakatsuya190511.entities.Category;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.enums.CategoryType;
import com.haradakatsuya190511.exceptions.CategoryNotFoundException;
import com.haradakatsuya190511.exceptions.InvalidCategoryException;
import com.haradakatsuya190511.repositories.CategoryRepository;
import com.haradakatsuya190511.services.CategoryService;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
	
	@Mock
	CategoryRepository categoryRepository;
	
	@InjectMocks
	CategoryService categoryService;
	
	@Test
	void getExpenseCategories_returnsSortedParentsAndChildren() {
		User user = new User();
		Category parent = new Category(user);
		parent.setName("Food");
		parent.setType(CategoryType.EXPENSE);
		parent.setId(1L);
		
		Category child = new Category(user);
		child.setName("Groceries");
		child.setType(CategoryType.EXPENSE);
		child.setParentCategory(parent);
		child.setId(2L);
		
		Category others = new Category(user);
		others.setName("Others");
		others.setType(CategoryType.EXPENSE);
		others.setId(3L);
		
		when(categoryRepository.findParentCategories(user, CategoryType.EXPENSE)).thenReturn(List.of(others, parent));
		when(categoryRepository.findChildCategories(user, CategoryType.EXPENSE)).thenReturn(List.of(child));
		
		List<CategoryResponseDto> result = categoryService.getExpenseCategories(user);
		
		assertEquals(3, result.size());
		assertEquals("Food", result.get(0).getName());
		assertEquals("Groceries", result.get(1).getName());
		assertEquals("Others", result.get(2).getName());
	}
	
	@Test
	void getIncomeCategories_returnsSortedParentsAndChildren() {
		User user = new User();
		Category parent = new Category(user);
		parent.setName("Salary");
		parent.setType(CategoryType.INCOME);
		parent.setId(1L);
		
		Category child = new Category(user);
		child.setName("Bonus");
		child.setType(CategoryType.INCOME);
		child.setParentCategory(parent);
		child.setId(2L);
		
		Category others = new Category(user);
		others.setName("Others");
		others.setType(CategoryType.INCOME);
		others.setId(3L);
		
		when(categoryRepository.findParentCategories(user, CategoryType.INCOME)).thenReturn(List.of(others, parent));
		when(categoryRepository.findChildCategories(user, CategoryType.INCOME)).thenReturn(List.of(child));
		
		List<CategoryResponseDto> result = categoryService.getIncomeCategories(user);
		
		assertEquals(3, result.size());
		assertEquals("Salary", result.get(0).getName());
		assertEquals("Bonus", result.get(1).getName());
		assertEquals("Others", result.get(2).getName());
	}
	
	@Test
	void getParentExpenseCategoriesWithoutOthers_returnsSortedParentsWithoutOthers() {
		User user = new User();
		Category food = new Category(user);
		food.setName("Food");
		food.setType(CategoryType.EXPENSE);
		food.setId(1L);
		
		Category supplies = new Category(user);
		supplies.setName("Supplies");
		supplies.setType(CategoryType.EXPENSE);
		supplies.setId(2L);
		
		Category others = new Category(user);
		others.setName("Others");
		others.setType(CategoryType.EXPENSE);
		others.setId(3L);
		
		when(categoryRepository.findParentCategories(user, CategoryType.EXPENSE)).thenReturn(List.of(others, food, supplies));
		
		List<CategoryResponseDto> result = categoryService.getParentExpenseCategoriesWithoutOthers(user);
		
		assertEquals(2, result.size());
		assertEquals("Food", result.get(0).getName());
		assertEquals("Supplies", result.get(1).getName());
	}
	
	@Test
	void getParentIncomeCategoriesWithoutOthers_returnsSortedParentsWithoutOthers() {
		User user = new User();
		Category salary = new Category(user);
		salary.setName("Salary");
		salary.setType(CategoryType.INCOME);
		salary.setId(1L);
		
		Category stock = new Category(user);
		stock.setName("Stock");
		stock.setType(CategoryType.INCOME);
		stock.setId(2L);
		
		Category others = new Category(user);
		others.setName("Others");
		others.setType(CategoryType.INCOME);
		others.setId(3L);
		
		when(categoryRepository.findParentCategories(user, CategoryType.INCOME)).thenReturn(List.of(others, salary, stock));
		
		List<CategoryResponseDto> result = categoryService.getParentIncomeCategoriesWithoutOthers(user);
		
		assertEquals(2, result.size());
		assertEquals("Salary", result.get(0).getName());
		assertEquals("Stock", result.get(1).getName());
	}
	
	@Test
	void getCategory_success() {
		User user = new User();
		user.setId(1L);
		Category cat = new Category(user);
		when(categoryRepository.findWithParentByIdAndUserId(1L, 1L)).thenReturn(Optional.of(cat));
		CategoryResponseDto dto = categoryService.getCategory(user, 1L);
		assertNotNull(dto.getUserId());
		assertEquals(dto.getUserId(), user.getId());
	}
	
	@Test
	void getCategory_notFound_throwsNotFound() {
		User user = new User();
		user.setId(1L);
		when(categoryRepository.findWithParentByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());
		assertThrows(CategoryNotFoundException.class, () -> categoryService.getCategory(user, 1L));
	}
	
	@Test
	void getCategory_userMismatch_throwsException() {
		User user = new User();
		user.setId(1L);
		User otherUser = new User();
		otherUser.setId(2L);
		
		Category cat = new Category(otherUser);
		
		when(categoryRepository.findWithParentByIdAndUserId(1L, 1L)).thenReturn(Optional.of(cat));
		
		assertThrows(CategoryNotFoundException.class, () -> categoryService.getCategory(user, 1L));
    }
	
	@Test
	void getCategoryDetail_withParent_success() {
		User user = new User();
		user.setId(1L);
		
		Category parent = new Category(user);
		parent.setId(1L);
		parent.setName("Parent");
		
		Category child = new Category(user);
		child.setId(2L);
		child.setParentCategory(parent);
		child.setName("Child");
		
		when(categoryRepository.findWithParentByIdAndUserId(2L, 1L)).thenReturn(Optional.of(child));
		when(categoryRepository.findById(1L)).thenReturn(Optional.of(parent));
		
		CategoryDetailResponseDto dto = categoryService.getCategoryDetail(user, 2L);
		assertNotNull(dto.getCategory().getId());
		assertEquals(dto.getCategory().getUserId(), user.getId());
		assertEquals("Child", dto.getCategory().getName());
		assertEquals("Parent", dto.getParentName());
	}
	
	@Test
	void getCategoryDetail_withNoParent_success() {
		User user = new User();
		user.setId(1L);
		
		Category category = new Category(user);
		category.setId(1L);
		category.setName("Category");
		
		when(categoryRepository.findWithParentByIdAndUserId(1L, 1L)).thenReturn(Optional.of(category));
		
		CategoryDetailResponseDto dto = categoryService.getCategoryDetail(user, 1L);
		assertNotNull(dto.getCategory().getId());
		assertEquals(dto.getCategory().getUserId(), user.getId());
		assertEquals("Category", dto.getCategory().getName());
		assertNull(dto.getParentName());
	}
	
	@Test
	void createCategory_withParent_succeeds() {
		User user = new User();
		user.setId(1L);
		Category parent = new Category(user);
		parent.setId(1L);
		parent.setName("Parent");
		
		CreateCategoryRequestDto dto = new CreateCategoryRequestDto();
		dto.setName("Child");
		dto.setParentId(1L);
		dto.setType(CategoryType.EXPENSE);
		
		when(categoryRepository.findById(1L)).thenReturn(Optional.of(parent));
		when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));
		
		CategoryResponseDto result = categoryService.createCategory(user, dto);
		
		assertEquals("Child", result.getName());
		assertEquals(CategoryType.EXPENSE, result.getType());
	}
	
	@Test
	void createCategory_withNoParent_succeeds() {
		User user = new User();
		user.setId(1L);
		
		CreateCategoryRequestDto dto = new CreateCategoryRequestDto();
		dto.setName("Category");
		dto.setType(CategoryType.EXPENSE);
		
		when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));
		
		CategoryResponseDto result = categoryService.createCategory(user, dto);
		
		assertEquals("Category", result.getName());
		assertEquals(CategoryType.EXPENSE, result.getType());
	}
	
	@Test
	void createCategory_notFound_throwsException() {
		User user = new User();
		
		CreateCategoryRequestDto dto = new CreateCategoryRequestDto();
		dto.setName("Category");
		dto.setParentId(1L);
		dto.setType(CategoryType.EXPENSE);
		
		when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
		
		assertThrows(CategoryNotFoundException.class, () -> categoryService.createCategory(user, dto));
	}
	
	@Test
	void updateCategory_notFound_throwsException() {
		User user = new User();
		when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
		
		UpdateCategoryRequestDto dto = new UpdateCategoryRequestDto();
		
		assertThrows(CategoryNotFoundException.class, () -> categoryService.updateCategory(user, 1L, dto));
	}
	
	@Test
	void applyCategoryInfo_invalidParent_throwsException() {
		User user = new User();
		user.setId(1L);
		
		User otherUser = new User();
		otherUser.setId(2L);
		
		Category parent = new Category(otherUser);
		parent.setId(1L);
		parent.setName("Parent");
		
		CreateCategoryRequestDto dto = new CreateCategoryRequestDto();
		dto.setParentId(1L);
		dto.setName("Child");
		dto.setType(CategoryType.EXPENSE);
		
		when(categoryRepository.findById(1L)).thenReturn(Optional.of(parent));
		
		assertThrows(InvalidCategoryException.class, () -> categoryService.createCategory(user, dto));
	}
}
