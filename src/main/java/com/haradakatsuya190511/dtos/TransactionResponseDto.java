package com.haradakatsuya190511.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.haradakatsuya190511.entities.Transaction;

public class TransactionResponseDto {
	private Long id;
	private Long userId;
	private Long categoryId;
	private BigDecimal amount;
	private String currency;
	private String description;
	private LocalDate transactionDate;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
	public TransactionResponseDto(Transaction transaction) {
		this.id = transaction.getId();
		this.userId = transaction.getUser().getId();
		this.categoryId = transaction.getCategory().getId();
		this.amount = transaction.getAmount();
		this.currency = transaction.getCurrency();
		this.description = transaction.getDescription();
		this.transactionDate = transaction.getTransactionDate();
		this.createdAt = transaction.getCreatedAt();
		this.updatedAt = transaction.getUpdatedAt();
	}
	
	public Long getId() {
		return id;
	}
	
	public Long getUserId() {
		return userId;
	}
	
	public Long getCategoryId() {
		return categoryId;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}
	
	public String getCurrency() {
		return currency;
	}
	
	public String getDescription() {
		return description;
	}
	
	public LocalDate getTransactionDate() {
		return transactionDate;
	}
	
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
}
