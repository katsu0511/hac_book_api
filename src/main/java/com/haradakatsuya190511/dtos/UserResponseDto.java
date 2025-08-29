package com.haradakatsuya190511.dtos;

import java.time.LocalDateTime;

import com.haradakatsuya190511.entities.User;

public class UserResponseDto {
	private Long id;
	private String name;
	private String email;
	private LocalDateTime createdAt;

	public UserResponseDto(User user) {
		this.id = user.getId();
		this.name = user.getName();
		this.email = user.getEmail();
		this.createdAt = user.getCreatedAt();
	}
	
	public Long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getEmail() {
		return email;
	}
	
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}
