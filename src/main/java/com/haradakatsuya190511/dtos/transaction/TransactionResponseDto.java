package com.haradakatsuya190511.dtos.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import com.haradakatsuya190511.entities.Transaction;
import com.haradakatsuya190511.enums.CategoryType;

public class TransactionResponseDto {
	private Long id;
	private Long userId;
	private Long categoryId;
	private String categoryName;
	private CategoryType categoryType;
	private BigDecimal amount;
	private String currency;
	private String description;
	private LocalDate transactionDate;
	private OffsetDateTime createdAt;
	private OffsetDateTime updatedAt;
	
	public TransactionResponseDto(Transaction transaction) {
		this.id = transaction.getId();
		this.userId = transaction.getUser().getId();
		this.categoryId = transaction.getCategory().getId();
		this.categoryName = transaction.getCategory().getName();
		this.categoryType = transaction.getCategory().getType();
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
	
	public String getCategoryName() {
		return categoryName;
	}
	
	public CategoryType getCategoryType() {
		return categoryType;
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
	
	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}
	
	public OffsetDateTime getUpdatedAt() {
		return updatedAt;
	}
}
