package com.haradakatsuya190511.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "transactions")
public class Transaction {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(precision = 10, scale = 2, nullable = false)
	@NotNull
	@Digits(integer = 8, fraction = 2)
	private BigDecimal amount;
	
	@Column(length = 3, nullable = false)
	@NotBlank
	@Pattern(regexp = "^[A-Z]{3}$")
	private String currency;
	
	@Column(length = 200)
	@Size(max = 200)
	private String description;
	
	@Column(nullable = false)
	private LocalDate transactionDate;
	
	@Column(insertable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@Column(insertable = false)
	private LocalDateTime updatedAt;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;
	
	public Transaction(User user, Category category, BigDecimal amount, String currency, String description, LocalDate transactionDate) {
		this.user = user;
		this.category = category;
		this.amount = amount;
		this.currency = currency;
		this.description = description;
		this.transactionDate = transactionDate;
	}
	
	public Long getId() {
		return id;
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
	
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	
	public User getUser() {
		return user;
	}
	
	public Category getCategory() {
		return category;
	}
	
	public void setCategory(Category category) {
		this.category = category;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Transaction)) return false;
		Transaction other = (Transaction) o;
		return id != null && id.equals(other.getId());
	}
	
	@Override
	public int hashCode() {
		return id == null ? 0 : id.hashCode();
	}
}
