package com.haradakatsuya190511.dtos.profile;

import com.haradakatsuya190511.entities.User;

public class ProfileResponseDto {
	private Long id;
	private String name;
	private String email;
	private String icon;
	private SettingResponseDto setting;

	public ProfileResponseDto(User user, SettingResponseDto setting) {
		this.id = user.getId();
		this.name = user.getName();
		this.email = user.getEmail();
		this.icon = user.getIcon();
		this.setting = setting;
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
	
	public String getIcon() {
		return icon;
	}
	
	public SettingResponseDto getSetting() {
		return setting;
	}
}
