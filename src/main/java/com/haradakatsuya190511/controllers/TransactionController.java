package com.haradakatsuya190511.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.haradakatsuya190511.dtos.AddTransactionRequestDto;
import com.haradakatsuya190511.dtos.ModifyTransactionRequestDto;
import com.haradakatsuya190511.dtos.TransactionResponseDto;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.services.AuthService;
import com.haradakatsuya190511.services.TransactionService;

@RestController
public class TransactionController {
	
	@Autowired
	AuthService authService;
	
	@Autowired
	TransactionService transactionService;
	
	@GetMapping("/transactions")
	public ResponseEntity<List<TransactionResponseDto>> getTransactions(Principal principal) {
		User user = authService.getUser(principal);
		return ResponseEntity.ok(transactionService.getTransactions(user));
	}
	
	@GetMapping("/transactions/{id}")
	public ResponseEntity<TransactionResponseDto> getTransaction(Principal principal, @PathVariable("id") Long id) {
		User user = authService.getUser(principal);
		return ResponseEntity.ok(transactionService.getTransaction(user, id));
	}
	
	@PostMapping("/transactions")
	public ResponseEntity<TransactionResponseDto> createTransaction(Principal principal, @RequestBody AddTransactionRequestDto request) {
		User user = authService.getUser(principal);
		return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.createTransaction(user, request));
	}
	
	@PutMapping("/transactions/{id}")
	public ResponseEntity<TransactionResponseDto> updateTransaction(Principal principal, @PathVariable("id") Long id, @RequestBody ModifyTransactionRequestDto request) {
		User user = authService.getUser(principal);
		return ResponseEntity.ok(transactionService.updateTransaction(user, id, request));
	}
}
