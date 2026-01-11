package com.haradakatsuya190511.services;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.haradakatsuya190511.dtos.transaction.AddTransactionRequestDto;
import com.haradakatsuya190511.dtos.transaction.ModifyTransactionRequestDto;
import com.haradakatsuya190511.dtos.transaction.TransactionResponseDto;
import com.haradakatsuya190511.dtos.transaction.shared.TransactionRequest;
import com.haradakatsuya190511.entities.Category;
import com.haradakatsuya190511.entities.Transaction;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.exceptions.CategoryNotFoundException;
import com.haradakatsuya190511.exceptions.TransactionNotFoundException;
import com.haradakatsuya190511.repositories.CategoryRepository;
import com.haradakatsuya190511.repositories.TransactionRepository;

@Service
public class TransactionService {
	
	@Autowired
	TransactionRepository transactionRepository;
	
	@Autowired
	CategoryRepository categoryRepository;
	
	public List<TransactionResponseDto> getTransactionsInPeriod(User user, LocalDate start, LocalDate end) {
		if (start == null || end == null) {
			YearMonth thisMonth = YearMonth.now();
			start = thisMonth.atDay(1);
			end = thisMonth.atEndOfMonth();
		}
		return transactionRepository.findAllWithCategoryInPeriod(user, start, end).stream()
			.map(TransactionResponseDto::new)
			.toList();
	}
	
	public TransactionResponseDto getTransaction(User user, Long id) {
		return transactionRepository.findByIdWithCategory(user, id)
			.map(TransactionResponseDto::new)
			.orElseThrow(TransactionNotFoundException::new);
	}
	
	public TransactionResponseDto createTransaction(User user, AddTransactionRequestDto request) {
		Transaction transaction = new Transaction(user);
		applyTransactionInfo(transaction, request);
		return new TransactionResponseDto(transactionRepository.save(transaction));
	}
	
	public TransactionResponseDto updateTransaction(User user, Long id, ModifyTransactionRequestDto request) {
		Transaction transaction = transactionRepository.findByIdWithCategory(user, id)
			.orElseThrow(TransactionNotFoundException::new);
		applyTransactionInfo(transaction, request);
		TransactionResponseDto dto = new TransactionResponseDto(transaction);
		transactionRepository.save(transaction);
		return dto;
	}
	
	public boolean deleteTransaction(User user, Long id) {
		Transaction transaction = transactionRepository.findByIdWithCategory(user, id)
			.orElseThrow(TransactionNotFoundException::new);
		transactionRepository.delete(transaction);
		return true;
	}
	
	private void applyTransactionInfo(Transaction transaction, TransactionRequest request) {
		Long categoryId = request.getCategoryId();
		Category category = categoryRepository.findById(categoryId).orElseThrow(CategoryNotFoundException::new);
		transaction.setCategory(category);
		transaction.setAmount(request.getAmount());
		transaction.setCurrency(request.getCurrency());
		transaction.setDescription(request.getDescription());
		transaction.setTransactionDate(request.getTransactionDate());
	}
}
