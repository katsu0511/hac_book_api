package com.haradakatsuya190511.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.haradakatsuya190511.dtos.transaction.CreateTransactionRequestDto;
import com.haradakatsuya190511.dtos.transaction.TransactionResponseDto;
import com.haradakatsuya190511.dtos.transaction.UpdateTransactionRequestDto;
import com.haradakatsuya190511.entities.Category;
import com.haradakatsuya190511.entities.Transaction;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.enums.CategoryType;
import com.haradakatsuya190511.exceptions.CategoryNotFoundException;
import com.haradakatsuya190511.exceptions.InvalidCategoryException;
import com.haradakatsuya190511.exceptions.TransactionNotFoundException;
import com.haradakatsuya190511.repositories.CategoryRepository;
import com.haradakatsuya190511.repositories.TransactionRepository;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
	
	@Mock
	TransactionRepository transactionRepository;
	
	@Mock
	CategoryRepository categoryRepository;
	
	@InjectMocks
	TransactionService transactionService;
	
	@Test
	void getTransactionsInPeriod_withDates_success() {
		User user = new User();
		user.setId(1L);
		LocalDate start = LocalDate.of(2024, 1, 1);
		LocalDate end = LocalDate.of(2024, 1, 31);
		
		Category category = new Category(user);
		category.setId(1L);
		category.setName("Category");
		category.setType(CategoryType.EXPENSE);
		
		Transaction tx = new Transaction(user);
		tx.setCategory(category);
		when(transactionRepository.findAllWithCategoryInPeriod(user, start, end)).thenReturn(List.of(tx));
		
		List<TransactionResponseDto> result = transactionService.getTransactionsInPeriod(user, start, end);
		
		assertEquals(1, result.size());
		assertEquals(1L, result.get(0).getCategoryId());
		assertEquals("Category", result.get(0).getCategoryName());
		assertEquals(CategoryType.EXPENSE, result.get(0).getCategoryType());
	}
	
	@Test
	void getTransactionsInPeriod_whenNull_usesThisMonth() {
		User user = new User();
		user.setId(1L);
		
		Category category = new Category(user);
		category.setId(1L);
		category.setName("Category");
		category.setType(CategoryType.EXPENSE);
		
		Transaction tx = new Transaction(user);
		tx.setCategory(category);
		when(transactionRepository.findAllWithCategoryInPeriod(eq(user), any(LocalDate.class), any(LocalDate.class))).thenReturn(List.of(tx));
		
		List<TransactionResponseDto> result = transactionService.getTransactionsInPeriod(user, null, null);
		
		assertEquals(1, result.size());
		assertEquals(1L, result.get(0).getCategoryId());
		assertEquals("Category", result.get(0).getCategoryName());
		assertEquals(CategoryType.EXPENSE, result.get(0).getCategoryType());
		verify(transactionRepository).findAllWithCategoryInPeriod(
	        eq(user),
	        eq(YearMonth.now().atDay(1)),
	        eq(YearMonth.now().atEndOfMonth())
	    );
	}
	
	@Test
	void getTransaction_success() {
		User user = new User();
		user.setId(1L);
		
		Category category = new Category(user);
		category.setId(1L);
		category.setName("Category");
		category.setType(CategoryType.EXPENSE);
		
		Transaction tx = new Transaction(user);
		tx.setCategory(category);
		when(transactionRepository.findWithCategoryByUserAndId(user, 1L)).thenReturn(Optional.of(tx));
		TransactionResponseDto dto = transactionService.getTransaction(user, 1L);
		assertNotNull(dto);
	}
	
	@Test
	void getTransaction_notFound_throwsException() {
		User user = new User();
		when(transactionRepository.findWithCategoryByUserAndId(user, 1L)).thenReturn(Optional.empty());
		assertThrows(TransactionNotFoundException.class, () ->
			transactionService.getTransaction(user, 1L)
		);
	}
	
	@Test
	void createTransaction_success() {
		User user = new User();
		user.setId(1L);
		
		Category category = new Category(user);
		category.setId(10L);
		
		CreateTransactionRequestDto request = new CreateTransactionRequestDto();
		request.setCategoryId(10L);
		request.setAmount(BigDecimal.valueOf(100));
		request.setCurrency("CAD");
		request.setTransactionDate(LocalDate.now());
		
		when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));
		when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
		
		TransactionResponseDto dto = transactionService.createTransaction(user, request);
		
		assertNotNull(dto);
		verify(transactionRepository).save(any(Transaction.class));
	}
	
	@Test
	void createTransaction_categoryNotFound_throwsException() {
		User user = new User();
		CreateTransactionRequestDto request = new CreateTransactionRequestDto();
		request.setCategoryId(99L);
		
		when(categoryRepository.findById(99L)).thenReturn(Optional.empty());
		
		assertThrows(CategoryNotFoundException.class, () ->
			transactionService.createTransaction(user, request)
		);
	}
	
	@Test
	void createTransaction_withOtherUsersCategory_throwsException() {
		User user = new User();
		user.setId(1L);
		
		User other = new User();
		other.setId(2L);
		
		Category category = new Category(other);
		category.setId(10L);
		
		CreateTransactionRequestDto request = new CreateTransactionRequestDto();
		request.setCategoryId(10L);
		
		when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));
		
		assertThrows(InvalidCategoryException.class, () ->
			transactionService.createTransaction(user, request)
		);
	}
	
	@Test
	void updateTransaction_success() {
		User user = new User();
		user.setId(1L);
		
		Category category = new Category(user);
		Transaction tx = new Transaction(user);
		tx.setCategory(category);
		
		UpdateTransactionRequestDto request = new UpdateTransactionRequestDto();
		request.setCategoryId(10L);
		
		when(transactionRepository.findWithCategoryByUserAndId(user, 1L)).thenReturn(Optional.of(tx));
		when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));
		
		TransactionResponseDto dto = transactionService.updateTransaction(user, 1L, request);
		
		assertNotNull(dto);
		verify(transactionRepository).save(tx);
	}
	
	@Test
	void deleteTransaction_success() {
		User user = new User();
		user.setId(1L);
		Category category = new Category(user);
		Transaction tx = new Transaction(user);
		tx.setCategory(category);
		
		when(transactionRepository.findWithCategoryByUserAndId(user, 1L)).thenReturn(Optional.of(tx));
		
		transactionService.deleteTransaction(user, 1L);
		
		verify(transactionRepository).delete(tx);
	}
}
