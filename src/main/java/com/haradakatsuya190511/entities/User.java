package com.haradakatsuya190511.entities;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(length = 32, nullable = false)
	@NotBlank
	@Size(max = 32)
	private String name;
	
	@Column(length = 100, nullable = false, unique = true)
	@Email
	@NotBlank
	@Size(max = 100)
	private String email;
	
	@Column(length = 255, nullable = false)
	@NotBlank
	@JsonIgnore
	private String password;
	
	@Column(name = "created_at", insertable = false, updatable = false)
	private OffsetDateTime createdAt;
	
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
	private Setting setting;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JsonIgnore
	private List<Category> categories = new ArrayList<>();
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JsonIgnore
	private List<Transaction> transactions = new ArrayList<>();
	
	public User() {}
	
	public User(String name, String email, String password) {
		this.name = name;
		this.email = email;
		this.password = password;
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
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}
	
	public Setting getSetting() {
		return setting;
	}
	
	public void setSetting(Setting setting) {
		this.setting = setting;
	}
	
	public List<Category> getCategories() {
	    return categories;
	}
	
	public void setCategories(List<Category> categories) {
		this.categories = categories;
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
		if (!(o instanceof User)) return false;
		User other = (User) o;
		return id != null && id.equals(other.getId());
	}
	
	@Override
	public int hashCode() {
		return id == null ? 0 : id.hashCode();
	}
}
