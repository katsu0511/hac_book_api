package com.haradakatsuya190511.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;

import com.haradakatsuya190511.exceptions.InvalidCategoryException;

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
	
	@Column(name = "transaction_date", nullable = false)
	@NotNull
	private LocalDate transactionDate;
	
	@Column(name = "created_at", insertable = false, updatable = false, nullable = false)
	private OffsetDateTime createdAt;
	
	@Column(name = "updated_at", insertable = false, updatable = false, nullable = false)
	private OffsetDateTime updatedAt;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;
	
	public Transaction() {}
	
	public Transaction(User user) {
		this.user = Objects.requireNonNull(user);
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
	
	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}
	
	public OffsetDateTime getUpdatedAt() {
		return updatedAt;
	}
	
	public User getUser() {
		return user;
	}
	
	public Category getCategory() {
		return category;
	}
	
	public void setCategory(Category category) {
		if (category == null || this.user != null && !category.getUser().getId().equals(this.user.getId())) {
			throw new InvalidCategoryException();
		}
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
