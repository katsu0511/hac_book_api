package com.haradakatsuya190511.dtos.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.haradakatsuya190511.dtos.transaction.shared.TransactionRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class UpdateTransactionRequestDto implements TransactionRequest {
	
	@NotNull
	private Long id;
	
	@NotNull
	private Long userId;
	
	@NotNull
	private Long categoryId;
	
	@NotNull
	@Positive
	private BigDecimal amount;
	
	@NotBlank
	@Pattern(regexp = "^[A-Z]{3}$")
	private String currency;
	
	@Size(max = 200)
	private String description;
	
	@NotNull
	private LocalDate transactionDate;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getUserId() {
		return userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public Long getCategoryId() {
		return categoryId;
	}
	
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}
	
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public String getCurrency() {
		return currency;
	}
	
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public LocalDate getTransactionDate() {
		return transactionDate;
	}
	
	public void setTransactionDate(LocalDate transactionDate) {
		this.transactionDate = transactionDate;
	}
}
