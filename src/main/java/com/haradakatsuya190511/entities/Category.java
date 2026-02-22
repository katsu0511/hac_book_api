package com.haradakatsuya190511.entities;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.haradakatsuya190511.enums.CategoryType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "categories")
public class Category {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(length = 100, nullable = false)
	@NotBlank
	@Size(max = 100)
	private String name;
	
	@Enumerated(EnumType.STRING)
	@JdbcTypeCode(SqlTypes.NAMED_ENUM)
	@Column(columnDefinition = "category_type", nullable = false)
	@NotNull
	private CategoryType type;
	
	@Column(length = 200)
	@Size(max = 200)
	private String description;
	
	@Column(nullable = false)
	private boolean isActive = true;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	@JsonIgnore
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	@JsonIgnore
	private Category parentCategory;
	
	@OneToMany(mappedBy = "parentCategory", fetch = FetchType.LAZY)
	@JsonIgnore
	private List<Category> childCategories = new ArrayList<>();
	
	@OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
	@JsonIgnore
	private List<Transaction> transactions = new ArrayList<>();
	
	public Category() {}
	
	public Category(User user) {
		this.user = user;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public CategoryType getType() {
		return type;
	}
	
	public void setType(CategoryType type) {
		this.type = type;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean isActive() {
		return isActive;
	}
	
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	public User getUser() {
		return user;
	}
	
	public Category getParentCategory() {
	    return parentCategory;
	}
	
	public void setParentCategory(Category parentCategory) {
		this.parentCategory = parentCategory;
	}
	
	public List<Category> getChildCategories() {
	    return childCategories;
	}
	
	public void setChildCategories(List<Category> childCategories) {
		this.childCategories = childCategories;
	}
	
	public List<Transaction> getTransactions() {
	    return transactions;
	}
	
	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Category)) return false;
		Category other = (Category) o;
		return id != null && id.equals(other.getId());
	}
	
	@Override
	public int hashCode() {
		return id == null ? 0 : id.hashCode();
	}
}
