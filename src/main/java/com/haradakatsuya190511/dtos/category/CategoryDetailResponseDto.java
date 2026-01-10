package com.haradakatsuya190511.dtos;

public class CategoryDetailResponseDto {
	private CategoryResponseDto category;
	private String parentName;
	
	public CategoryDetailResponseDto(CategoryResponseDto category, String parentName) {
		this.category = category;
		this.parentName = parentName;
	}
	
	public CategoryResponseDto getCategory() {
		return category;
	}
	
	public String getParentName() {
		return parentName;
	}
}
