package com.haradakatsuya190511.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.haradakatsuya190511.dtos.AddTransactionRequestDto;
import com.haradakatsuya190511.dtos.TransactionResponseDto;
import com.haradakatsuya190511.entities.Category;
import com.haradakatsuya190511.entities.Transaction;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.exceptions.CategoryNotFoundException;
import com.haradakatsuya190511.repositories.CategoryRepository;
import com.haradakatsuya190511.repositories.TransactionRepository;

@Service
public class TransactionService {
	
	@Autowired
	TransactionRepository transactionRepository;
	
	@Autowired
	CategoryRepository categoryRepository;
	
	public List<TransactionResponseDto> getTransactions(User user) {
		return transactionRepository.findByUser(user).stream()
			.map(TransactionResponseDto::new)
			.toList();
	}
	
	public TransactionResponseDto createTransaction(User user, AddTransactionRequestDto request) {
		Transaction transaction = new Transaction(user);
		Long categoryId = request.getCategoryId();
		Category category = categoryRepository.findById(categoryId).orElseThrow(CategoryNotFoundException::new);
		transaction.setCategory(category);
		transaction.setAmount(request.getAmount());
		transaction.setCurrency(request.getCurrency());
		transaction.setDescription(request.getDescription());
		transaction.setTransactionDate(request.getTransactionDate());
		return new TransactionResponseDto(transactionRepository.save(transaction));
	}
}
