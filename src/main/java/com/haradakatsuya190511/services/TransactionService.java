package com.haradakatsuya190511.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.haradakatsuya190511.dtos.TransactionResponseDto;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.repositories.TransactionRepository;

@Service
public class TransactionService {
	
	@Autowired
	TransactionRepository transactionRepository;
	
	public List<TransactionResponseDto> getTransactions(User user) {
		return transactionRepository.findByUser(user).stream()
			.map(TransactionResponseDto::new)
			.toList();
	}
}
