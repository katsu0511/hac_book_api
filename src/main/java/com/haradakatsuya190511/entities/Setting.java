package com.haradakatsuya190511.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "settings")
public class Setting {
	
	@Id
	@Column(name = "user_id")
	private Long userId;
	
	@Column(length = 32, nullable = false)
	@NotBlank
	@Size(max = 32)
	private String language = "English";
	
	@Column(length = 3, nullable = false)
	@NotBlank
	@Pattern(regexp = "^[A-Z]{3}$")
	private String currency = "CAD";
	
	@OneToOne(optional = false)
	@MapsId
	@JoinColumn(name = "user_id")
	@JsonIgnore
	private User user;
	
	public Setting() {}
	
	public Setting(User user) {
		this.user = user;
	}
	
	public Long getUserId() {
		return userId;
	}
	
	public String getLanguage() {
		return language;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}
	
	public String getCurrency() {
		return currency;
	}
	
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	public User getUser() {
		return user;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Setting)) return false;
		Setting other = (Setting) o;
		return userId != null && userId.equals(other.userId);
	}
	
	@Override
	public int hashCode() {
		return userId == null ? 0 : userId.hashCode();
	}
}
