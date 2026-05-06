package com.haradakatsuya190511.dtos.profile;

public class ProfileResponseDto {
	private UserForProfileResponseDto user;
	private SettingResponseDto setting;

	public ProfileResponseDto(UserForProfileResponseDto user, SettingResponseDto setting) {
		this.user = user;
		this.setting = setting;
	}
	
	public UserForProfileResponseDto getUser() {
		return user;
	}
	
	public SettingResponseDto getSetting() {
		return setting;
	}
}
