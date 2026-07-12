package com.haradakatsuya190511.dtos.profile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdatePasswordRequestDto {
	
	@NotBlank
	@Size(max = 255)
	private String oldPassword;
	
	@NotBlank
	@Size(max = 255)
	private String newPassword;
	
	public String getOldPassword() {
		return oldPassword;
	}
	
	public String getNewPassword() {
		return newPassword;
	}
	
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}
	
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
}
