package com.haradakatsuya190511.slice.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.haradakatsuya190511.controllers.TransactionController;
import com.haradakatsuya190511.dtos.transaction.TransactionResponseDto;
import com.haradakatsuya190511.entities.Category;
import com.haradakatsuya190511.entities.Transaction;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.enums.CategoryType;
import com.haradakatsuya190511.services.CategoryService;
import com.haradakatsuya190511.services.TransactionService;

@WebMvcTest(TransactionController.class)
@Import(AuthControllerTest.TestSecurityConfig.class)
class TransactionControllerTest {
	
	@Autowired
	MockMvc mockMvc;
	
	@MockitoBean
	CategoryService categoryService;
	
	@MockitoBean
	TransactionService transactionService;
	
	@Test
	@WithMockUser
	void getTransactions_succeedAndShouldReturn200_whenNoParams() throws Exception {
		given(transactionService.getTransactionsInPeriod(any(), isNull(), isNull())).willReturn(List.of());
		
		mockMvc.perform(get("/transactions"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray());
		
		verify(transactionService).getTransactionsInPeriod(any(), isNull(), isNull());
	}
	
	@Test
	@WithMockUser
	void getTransactions_succeedAndShouldReturn200_whenWithParams() throws Exception {
		given(transactionService.getTransactionsInPeriod(any(), eq(LocalDate.of(2026,1,1)), eq(LocalDate.of(2026,1,31)))).willReturn(List.of());
		
		mockMvc.perform(get("/transactions")
			.param("start", "2026-01-01")
			.param("end", "2026-01-31"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isArray());
		
		verify(transactionService).getTransactionsInPeriod(any(), eq(LocalDate.of(2026,1,1)), eq(LocalDate.of(2026,1,31)));
	}
	
	@Test
	@WithMockUser
	void getTransactions_failAndShouldReturn400_whenInvalid() throws Exception {
		mockMvc.perform(get("/transactions")
			.param("start", "invalid-date")
			.param("end", "invalid-date"))
			.andExpect(status().isBadRequest());
		verify(transactionService, never()).getTransactionsInPeriod(any(), any(), any());
	}
	
	@Test
	void getTransactions_failAndShouldReturn403_whenUnauthenticated() throws Exception {
		mockMvc.perform(get("/transactions")).andExpect(status().isForbidden());
		verify(transactionService, never()).getTransactionsInPeriod(any(), any(), any());
	}
	
	@Test
	@WithMockUser
	void getTransaction_succeedAndShouldReturn200() throws Exception {
		User user = new User();
		user.setId(1L);
		
		Category category = new Category(user);
		category.setId(1L);
		category.setName("Food");
		category.setType(CategoryType.EXPENSE);
		
		Transaction tx = new Transaction(user);
		tx.setCategory(category);
		tx.setId(1L);
		tx.setAmount(new BigDecimal("100.00"));
		tx.setCurrency("CAD");
		tx.setDescription("Lunch");
		tx.setTransactionDate(LocalDate.of(2026, 1, 1));
		TransactionResponseDto dto = new TransactionResponseDto(tx);
		
		given(transactionService.getTransaction(any(), eq(1L))).willReturn(dto);
		
		mockMvc.perform(get("/transactions/{id}", 1L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").isNumber())
			.andExpect(jsonPath("$.userId").isNumber())
			.andExpect(jsonPath("$.categoryId").isNumber())
			.andExpect(jsonPath("$.categoryName").isString())
			.andExpect(jsonPath("$.categoryType").isString())
			.andExpect(jsonPath("$.amount").isNumber())
			.andExpect(jsonPath("$.currency").isString())
			.andExpect(jsonPath("$.description").isString())
			.andExpect(jsonPath("$.transactionDate").isString())
			.andExpect(jsonPath("$.createdAt").doesNotExist())
			.andExpect(jsonPath("$.updatedAt").doesNotExist());
		
		verify(transactionService).getTransaction(any(), eq(1L));
	}
	
	@Test
	void getTransaction_failAndShouldReturn403_whenUnauthenticated() throws Exception {
		mockMvc.perform(get("/transactions/{id}", 1L)).andExpect(status().isForbidden());
		verify(transactionService, never()).getTransaction(any(), any());
	}
	
	@Test
	@WithMockUser
	void getTransactionForEdit_succeedAndShouldReturn200() throws Exception {
		User user = new User();
		user.setId(1L);
		
		Category category = new Category(user);
		category.setId(1L);
		category.setName("Food");
		category.setType(CategoryType.EXPENSE);
		
		Transaction tx = new Transaction(user);
		tx.setCategory(category);
		tx.setId(1L);
		tx.setAmount(new BigDecimal("100.00"));
		tx.setCurrency("CAD");
		tx.setDescription("Lunch");
		tx.setTransactionDate(LocalDate.of(2026, 1, 1));
		TransactionResponseDto dto = new TransactionResponseDto(tx);
		
		given(categoryService.getExpenseCategories(any())).willReturn(List.of());
		given(categoryService.getIncomeCategories(any())).willReturn(List.of());
		given(transactionService.getTransaction(any(), eq(1L))).willReturn(dto);
		
		mockMvc.perform(get("/transactions/{id}/edit", 1L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.transaction.id").isNumber())
			.andExpect(jsonPath("$.transaction.userId").isNumber())
			.andExpect(jsonPath("$.transaction.categoryId").isNumber())
			.andExpect(jsonPath("$.transaction.categoryName").isString())
			.andExpect(jsonPath("$.transaction.categoryType").isString())
			.andExpect(jsonPath("$.transaction.amount").isNumber())
			.andExpect(jsonPath("$.transaction.currency").isString())
			.andExpect(jsonPath("$.transaction.description").isString())
			.andExpect(jsonPath("$.transaction.transactionDate").isString())
			.andExpect(jsonPath("$.transaction.createdAt").doesNotExist())
			.andExpect(jsonPath("$.transaction.updatedAt").doesNotExist())
			.andExpect(jsonPath("$.categories.expense").isArray())
			.andExpect(jsonPath("$.categories.income").isArray());
		
		verify(categoryService).getExpenseCategories(any());
		verify(categoryService).getIncomeCategories(any());
		verify(transactionService).getTransaction(any(), eq(1L));
	}
	
	@Test
	void getTransactionForEdit_failAndShouldReturn403_whenUnauthenticated() throws Exception {
		mockMvc.perform(get("/transactions/{id}/edit", 1L)).andExpect(status().isForbidden());
		verify(categoryService, never()).getExpenseCategories(any());
		verify(categoryService, never()).getIncomeCategories(any());
		verify(transactionService, never()).getTransaction(any(), any());
	}
	
	@Test
	@WithMockUser
	void createTransaction_succeedAndShouldReturn201() throws Exception {
		User user = new User();
		user.setId(1L);
		
		Category category = new Category(user);
		category.setId(1L);
		category.setName("Food");
		category.setType(CategoryType.EXPENSE);
		
		Transaction tx = new Transaction(user);
		tx.setCategory(category);
		tx.setId(1L);
		tx.setAmount(new BigDecimal("100.00"));
		tx.setCurrency("CAD");
		tx.setDescription("Lunch");
		tx.setTransactionDate(LocalDate.of(2026, 1, 1));
		TransactionResponseDto dto = new TransactionResponseDto(tx);
		
		given(transactionService.createTransaction(any(), any())).willReturn(dto);
		
		mockMvc.perform(post("/transactions")
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
				{
					"categoryId": 1,
					"amount": 100.00,
					"currency": "CAD",
					"description": "Lunch",
					"transactionDate": "2026-01-01"
				}
			"""))
		.andExpect(status().isCreated())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.id").isNumber())
		.andExpect(jsonPath("$.userId").isNumber())
		.andExpect(jsonPath("$.categoryId").isNumber())
		.andExpect(jsonPath("$.categoryName").isString())
		.andExpect(jsonPath("$.categoryType").isString())
		.andExpect(jsonPath("$.amount").isNumber())
		.andExpect(jsonPath("$.currency").isString())
		.andExpect(jsonPath("$.description").isString())
		.andExpect(jsonPath("$.transactionDate").isString())
		.andExpect(jsonPath("$.createdAt").doesNotExist())
		.andExpect(jsonPath("$.updatedAt").doesNotExist());
		
		verify(transactionService).createTransaction(any(), any());
	}
	
	@Test
	@WithMockUser
	void createTransaction_failAndShouldReturn400_whenInvalid() throws Exception {
		mockMvc.perform(post("/transactions")
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
				{
					"categoryId": 1,
					"amount": -1,
					"currency": "CAD",
					"description": "Lunch",
					"transactionDate": "2026-01-01"
				}
			"""))
			.andExpect(status().isBadRequest());
		
		verify(transactionService, never()).createTransaction(any(), any());
	}
	
	@Test
	void createTransaction_failAndShouldReturn403_whenUnauthenticated() throws Exception {
		mockMvc.perform(post("/transactions")).andExpect(status().isForbidden());
		verify(transactionService, never()).createTransaction(any(), any());
	}
	
	@Test
	@WithMockUser
	void updateTransaction_succeedAndShouldReturn200() throws Exception {
		User user = new User();
		user.setId(1L);
		
		Category category = new Category(user);
		category.setId(1L);
		category.setName("Food");
		category.setType(CategoryType.EXPENSE);
		
		Transaction tx = new Transaction(user);
		tx.setCategory(category);
		tx.setId(1L);
		tx.setAmount(new BigDecimal("100.00"));
		tx.setCurrency("CAD");
		tx.setDescription("Lunch");
		tx.setTransactionDate(LocalDate.of(2026, 1, 1));
		TransactionResponseDto dto = new TransactionResponseDto(tx);
		
		given(transactionService.updateTransaction(any(), eq(1L), any())).willReturn(dto);
		
		mockMvc.perform(put("/transactions/{id}", 1L)
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
				{
					"id": 1,
					"categoryId": 1,
					"amount": 100.00,
					"currency": "CAD",
					"description": "Lunch",
					"transactionDate": "2026-01-01"
				}
			"""))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.id").isNumber())
		.andExpect(jsonPath("$.userId").isNumber())
		.andExpect(jsonPath("$.categoryId").isNumber())
		.andExpect(jsonPath("$.categoryName").isString())
		.andExpect(jsonPath("$.categoryType").isString())
		.andExpect(jsonPath("$.amount").isNumber())
		.andExpect(jsonPath("$.currency").isString())
		.andExpect(jsonPath("$.description").isString())
		.andExpect(jsonPath("$.transactionDate").isString())
		.andExpect(jsonPath("$.createdAt").doesNotExist())
		.andExpect(jsonPath("$.updatedAt").doesNotExist());
		
		verify(transactionService).updateTransaction(any(), eq(1L), any());
	}
	
	@Test
	@WithMockUser
	void updateTransaction_failAndShouldReturn400_whenInvalid() throws Exception {
		mockMvc.perform(put("/transactions/{id}", 1L)
			.contentType(MediaType.APPLICATION_JSON)
			.content("""
				{
					"id": 1,
					"categoryId": 1,
					"amount": -1,
					"currency": "CAD",
					"description": "Lunch",
					"transactionDate": "2026-01-01"
				}
			"""))
			.andExpect(status().isBadRequest());
		
		verify(transactionService, never()).updateTransaction(any(), eq(1L), any());
	}
	
	@Test
	void updateTransaction_failAndShouldReturn403_whenUnauthenticated() throws Exception {
		mockMvc.perform(put("/transactions/{id}", 1L)).andExpect(status().isForbidden());
		verify(transactionService, never()).updateTransaction(any(), eq(1L), any());
	}
	
	@Test
	@WithMockUser
	void deleteTransaction_succeedAndShouldReturn204() throws Exception {
		mockMvc.perform(delete("/transactions/{id}", 1L)).andExpect(status().isNoContent());
		verify(transactionService).deleteTransaction(any(), eq(1L));
	}
	
	@Test
	void deleteTransaction_failAndShouldReturn403_whenUnauthenticated() throws Exception {
		mockMvc.perform(delete("/transactions/{id}", 1L)).andExpect(status().isForbidden());
		verify(transactionService, never()).deleteTransaction(any(), eq(1L));
	}
}
