package com.haradakatsuya190511.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.haradakatsuya190511.dtos.category.CategoryDetailResponseDto;
import com.haradakatsuya190511.dtos.category.CategoryResponseDto;
import com.haradakatsuya190511.entities.Category;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.enums.CategoryType;
import com.haradakatsuya190511.services.CategoryService;

@WebMvcTest(CategoryController.class)
@ActiveProfiles("test")
@Import(AuthControllerTest.TestSecurityConfig.class)
class CategoryControllerTest {
	
	@Autowired
	MockMvc mockMvc;
	
	@MockitoBean
	CategoryService categoryService;
	
	@Test
	@WithMockUser
	void getCategories_succeedAndShouldReturn200() throws Exception {
		given(categoryService.getExpenseCategories(any(User.class))).willReturn(List.of());
		given(categoryService.getIncomeCategories(any(User.class))).willReturn(List.of());
		
		mockMvc.perform(get("/categories"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.expense").isArray())
			.andExpect(jsonPath("$.income").isArray());
		
		verify(categoryService).getExpenseCategories(any());
		verify(categoryService).getIncomeCategories(any());
	}
	
	@Test
	void getCategories_failAndShouldReturn403_whenUnauthenticated() throws Exception {
		mockMvc.perform(get("/categories")).andExpect(status().isForbidden());
		verify(categoryService, never()).getExpenseCategories(any());
		verify(categoryService, never()).getIncomeCategories(any());
	}
	
	@Test
	@WithMockUser
	void getParentCategories_succeedAndShouldReturn200() throws Exception {
		given(categoryService.getParentExpenseCategoriesWithoutOthers(any(User.class))).willReturn(List.of());
		given(categoryService.getParentIncomeCategoriesWithoutOthers(any(User.class))).willReturn(List.of());
		
		mockMvc.perform(get("/parent-categories"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.expense").isArray())
			.andExpect(jsonPath("$.income").isArray());
		
		verify(categoryService).getParentExpenseCategoriesWithoutOthers(any());
		verify(categoryService).getParentIncomeCategoriesWithoutOthers(any());
	}
	
	@Test
	void getParentCategories_failAndShouldReturn403_whenUnauthenticated() throws Exception {
		mockMvc.perform(get("/parent-categories")).andExpect(status().isForbidden());
		verify(categoryService, never()).getParentExpenseCategoriesWithoutOthers(any());
		verify(categoryService, never()).getParentIncomeCategoriesWithoutOthers(any());
	}
	
	@Test
	@WithMockUser
	void getCategory_succeedAndShouldReturn200() throws Exception {
		User user = new User();
		user.setId(1L);
		
		Category category = new Category(user);
		category.setId(1L);
		category.setParentCategory(null);
		category.setName("Food");
		category.setType(CategoryType.EXPENSE);
		CategoryResponseDto response = new CategoryResponseDto(category);
		CategoryDetailResponseDto dto = new CategoryDetailResponseDto(response, "parentName");
		
		given(categoryService.getCategoryDetail(any(), eq(1L))).willReturn(dto);
		
		mockMvc.perform(get("/categories/{id}", 1L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.category.id").isNumber())
			.andExpect(jsonPath("$.category.userId").isNumber())
			.andExpect(jsonPath("$.category.parentId").doesNotExist())
			.andExpect(jsonPath("$.category.name").isString())
			.andExpect(jsonPath("$.category.type").isString())
			.andExpect(jsonPath("$.category.description").doesNotExist())
			.andExpect(jsonPath("$.category.active").isBoolean())
			.andExpect(jsonPath("$.parentName").isString());
		
		verify(categoryService).getCategoryDetail(any(), eq(1L));
	}
	
	@Test
	void getCategory_failAndShouldReturn403_whenUnauthenticated() throws Exception {
		mockMvc.perform(get("/categories/{id}", 1L)).andExpect(status().isForbidden());
		verify(categoryService, never()).getCategoryDetail(any(), eq(1L));
	}
	

	@Test
	@WithMockUser
	void getCategoryForEdit_succeedAndShouldReturn200() throws Exception {
		User user = new User();
		user.setId(1L);
		
		Category category = new Category(user);
		category.setId(1L);
		category.setParentCategory(null);
		category.setName("Food");
		category.setType(CategoryType.EXPENSE);
		CategoryResponseDto dto = new CategoryResponseDto(category);
		
		given(categoryService.getParentExpenseCategoriesWithoutOthers(any())).willReturn(List.of());
		given(categoryService.getParentIncomeCategoriesWithoutOthers(any())).willReturn(List.of());
		given(categoryService.getCategory(any(), eq(1L))).willReturn(dto);
		
		mockMvc.perform(get("/categories/{id}/edit", 1L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.category.id").isNumber())
			.andExpect(jsonPath("$.category.userId").isNumber())
			.andExpect(jsonPath("$.category.parentId").doesNotExist())
			.andExpect(jsonPath("$.category.name").isString())
			.andExpect(jsonPath("$.category.type").isString())
			.andExpect(jsonPath("$.category.description").doesNotExist())
			.andExpect(jsonPath("$.category.active").isBoolean())
			.andExpect(jsonPath("$.categories.expense").isArray())
			.andExpect(jsonPath("$.categories.income").isArray());
		
		verify(categoryService).getParentExpenseCategoriesWithoutOthers(any());
		verify(categoryService).getParentIncomeCategoriesWithoutOthers(any());
		verify(categoryService).getCategory(any(), eq(1L));
	}
	
	@Test
	void getCategoryForEdit_failAndShouldReturn403_whenUnauthenticated() throws Exception {
		mockMvc.perform(get("/categories/{id}/edit", 1L)).andExpect(status().isForbidden());
		verify(categoryService, never()).getParentExpenseCategoriesWithoutOthers(any());
		verify(categoryService, never()).getParentIncomeCategoriesWithoutOthers(any());
		verify(categoryService, never()).getCategory(any(), eq(1L));
	}
	
	@Test
	@WithMockUser
	void createCategory_succeedAndShouldReturn201() throws Exception {
		User user = new User();
		user.setId(1L);
		
		Category category = new Category(user);
		category.setId(1L);
		category.setParentCategory(null);
		category.setName("Food");
		category.setType(CategoryType.EXPENSE);
		CategoryResponseDto dto = new CategoryResponseDto(category);
		
		given(categoryService.createCategory(any(), any())).willReturn(dto);
		
		mockMvc.perform(post("/categories")
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
				{
					"parentId": null,
					"name": "Food",
					"type": "EXPENSE",
					"description": null
				}
			"""))
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").isNumber())
			.andExpect(jsonPath("$.userId").isNumber())
			.andExpect(jsonPath("$.parentId").doesNotExist())
			.andExpect(jsonPath("$.name").isString())
			.andExpect(jsonPath("$.type").isString())
			.andExpect(jsonPath("$.description").doesNotExist())
			.andExpect(jsonPath("$.active").isBoolean());
		
		verify(categoryService).createCategory(any(), any());
	}
	
	@Test
	@WithMockUser
	void createCategory_failAndShouldReturn400_whenInvalid() throws Exception {
		mockMvc.perform(post("/categories")
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
				{
					"parentId": null,
					"name": "",
					"type": "EXPENSE",
					"description": null
				}
			"""))
			.andExpect(status().isBadRequest());
		
		verify(categoryService, never()).createCategory(any(), any());
	}
	
	@Test
	void createCategory_failAndShouldReturn403_whenUnauthenticated() throws Exception {
		mockMvc.perform(post("/categories")).andExpect(status().isForbidden());
		verify(categoryService, never()).createCategory(any(), any());
	}
	
	@Test
	@WithMockUser
	void updateCategory_succeedAndShouldReturn200() throws Exception {
		User user = new User();
		user.setId(1L);
		
		Category category = new Category(user);
		category.setId(1L);
		category.setParentCategory(null);
		category.setName("Food");
		category.setType(CategoryType.EXPENSE);
		CategoryResponseDto dto = new CategoryResponseDto(category);
		
		given(categoryService.updateCategory(any(), any(), any())).willReturn(dto);
		
		mockMvc.perform(put("/categories/{id}", 1L)
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
				{
					"id": 1,
					"parentId": null,
					"name": "Food",
					"type": "EXPENSE",
					"description": null
				}
			"""))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.id").isNumber())
			.andExpect(jsonPath("$.userId").isNumber())
			.andExpect(jsonPath("$.parentId").doesNotExist())
			.andExpect(jsonPath("$.name").isString())
			.andExpect(jsonPath("$.type").isString())
			.andExpect(jsonPath("$.description").doesNotExist())
			.andExpect(jsonPath("$.active").isBoolean());
		
		verify(categoryService).updateCategory(any(), any(), any());
	}
	
	@Test
	@WithMockUser
	void updateCategory_failAndShouldReturn400_whenInvalid() throws Exception {
		mockMvc.perform(put("/categories/{id}", 1L)
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
				{
					"id": 1,
					"parentId": null,
					"name": "",
					"type": "EXPENSE",
					"description": null
				}
			"""))
			.andExpect(status().isBadRequest());
		
		verify(categoryService, never()).updateCategory(any(), any(), any());
	}
	
	@Test
	void updateCategory_failAndShouldReturn403_whenUnauthenticated() throws Exception {
		mockMvc.perform(put("/categories/{id}", 1L)).andExpect(status().isForbidden());
		verify(categoryService, never()).updateCategory(any(), any(), any());
	}
}
