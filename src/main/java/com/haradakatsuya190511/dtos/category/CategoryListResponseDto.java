package com.haradakatsuya190511.dtos.category;

import java.util.List;

public class CategoryListResponseDto {
	private List<CategoryResponseDto> expense;
	private List<CategoryResponseDto> income;
	
	public CategoryListResponseDto(List<CategoryResponseDto> expense, List<CategoryResponseDto> income) {
		this.expense = expense;
		this.income = income;
	}
	
	public List<CategoryResponseDto> getExpense() {
		return expense;
	}
	
	public List<CategoryResponseDto> getIncome() {
		return income;
	}
}
