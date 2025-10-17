package com.haradakatsuya190511.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.haradakatsuya190511.dtos.AddTransactionRequestDto;
import com.haradakatsuya190511.dtos.ModifyTransactionRequestDto;
import com.haradakatsuya190511.dtos.TransactionResponseDto;
import com.haradakatsuya190511.dtos.shared.TransactionRequest;
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
	
	public List<TransactionResponseDto> getTransactions(User user) {
		return transactionRepository.findByUser(user).stream()
			.map(TransactionResponseDto::new)
			.toList();
	}
	
	public TransactionResponseDto getTransaction(User user, Long id) {
		return transactionRepository.findById(id)
			.filter(t -> t.getUser().getId().equals(user.getId()))
			.map(TransactionResponseDto::new)
			.orElseThrow(TransactionNotFoundException::new);
	}
	
	public TransactionResponseDto createTransaction(User user, AddTransactionRequestDto request) {
		Transaction transaction = new Transaction(user);
		applyTransactionInfo(transaction, request);
		return new TransactionResponseDto(transactionRepository.save(transaction));
	}
	
	public TransactionResponseDto updateTransaction(User user, Long id, ModifyTransactionRequestDto request) {
		Transaction transaction = transactionRepository.findById(id)
				.filter(t -> t.getId().equals(request.getId()))
				.filter(t -> t.getUser().getId().equals(user.getId()))
				.orElseThrow(TransactionNotFoundException::new);
		applyTransactionInfo(transaction, request);
		return new TransactionResponseDto(transactionRepository.save(transaction));
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
