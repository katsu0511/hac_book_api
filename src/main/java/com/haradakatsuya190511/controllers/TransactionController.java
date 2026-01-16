package com.haradakatsuya190511.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.haradakatsuya190511.dtos.category.CategoryListResponseDto;
import com.haradakatsuya190511.dtos.transaction.CreateTransactionRequestDto;
import com.haradakatsuya190511.dtos.transaction.UpdateTransactionRequestDto;
import com.haradakatsuya190511.dtos.transaction.TransactionForEditResponseDto;
import com.haradakatsuya190511.dtos.transaction.TransactionResponseDto;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.services.CategoryService;
import com.haradakatsuya190511.services.TransactionService;

import jakarta.validation.Valid;

@RestController
public class TransactionController {
	
	private final CategoryService categoryService;
	private final TransactionService transactionService;
	
	public TransactionController(CategoryService categoryService, TransactionService transactionService) {
		this.categoryService = categoryService;
		this.transactionService = transactionService;
	}
	
	@GetMapping("/transactions")
	public ResponseEntity<List<TransactionResponseDto>> getTransactions(
		@AuthenticationPrincipal User user,
		@RequestParam(value = "start", required = false) LocalDate start,
		@RequestParam(value = "end", required = false) LocalDate end
	) {
		return ResponseEntity.ok(transactionService.getTransactionsInPeriod(user, start, end));
	}
	
	@GetMapping("/transactions/{id}")
	public ResponseEntity<TransactionResponseDto> getTransaction(@AuthenticationPrincipal User user, @PathVariable("id") Long id) {
		return ResponseEntity.ok(transactionService.getTransaction(user, id));
	}
	
	@GetMapping("/transactions/{id}/edit")
	public ResponseEntity<TransactionForEditResponseDto> getTransactionForEdit(@AuthenticationPrincipal User user, @PathVariable("id") Long id) {
		CategoryListResponseDto categories = new CategoryListResponseDto(categoryService.getExpenseCategories(user), categoryService.getIncomeCategories(user));
		return ResponseEntity.ok(new TransactionForEditResponseDto(transactionService.getTransaction(user, id), categories));
	}
	
	@PostMapping("/transactions")
	public ResponseEntity<TransactionResponseDto> createTransaction(@AuthenticationPrincipal User user, @Valid @RequestBody CreateTransactionRequestDto request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.createTransaction(user, request));
	}
	
	@PutMapping("/transactions/{id}")
	public ResponseEntity<TransactionResponseDto> updateTransaction(@AuthenticationPrincipal User user, @PathVariable("id") Long id, @Valid @RequestBody UpdateTransactionRequestDto request) {
		return ResponseEntity.ok(transactionService.updateTransaction(user, id, request));
	}
	
	@DeleteMapping("/transactions/{id}")
	public ResponseEntity<?> deleteTransaction(@AuthenticationPrincipal User user, @PathVariable("id") Long id) {
		transactionService.deleteTransaction(user, id);
		return ResponseEntity.noContent().build();
	}
}
