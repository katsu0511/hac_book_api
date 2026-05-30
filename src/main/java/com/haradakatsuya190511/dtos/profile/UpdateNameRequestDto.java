package com.haradakatsuya190511.dtos.profile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateNameRequestDto {
	
	@NotBlank
	@Size(max = 32)
	private String name;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
