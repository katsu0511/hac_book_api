package com.haradakatsuya190511.dtos;

import com.haradakatsuya190511.validations.Password;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserRequestDto {
	@NotBlank
	private String name;
	
	@Email
	@NotBlank
	private String email;
	
	@Password
	private String password;
	
	public UserRequestDto() {}
	
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
}
