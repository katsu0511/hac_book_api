package com.haradakatsuya190511.dtos.profile;

import java.math.BigDecimal;

import com.haradakatsuya190511.entities.Setting;

public class SettingResponseDto {
	private Long userId;
	private String language;
	private String currency;
	private BigDecimal monthlySavingGoal;

	public SettingResponseDto(Setting setting) {
		this.userId = setting.getUserId();
		this.language = setting.getLanguage();
		this.currency = setting.getCurrency();
		this.monthlySavingGoal = setting.getMonthlySavingGoal();
	}
	
	public Long getUserId() {
		return userId;
	}
	
	public String getLanguage() {
		return language;
	}
	
	public String getCurrency() {
		return currency;
	}
	
	public BigDecimal getMonthlySavingGoal() {
		return monthlySavingGoal;
	}
}
