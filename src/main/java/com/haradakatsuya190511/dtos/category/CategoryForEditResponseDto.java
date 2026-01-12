package com.haradakatsuya190511.dtos.category;

public class CategoryForEditResponseDto {
	private CategoryResponseDto category;
	private CategoryListResponseDto categories;
	
	public CategoryForEditResponseDto(CategoryResponseDto category, CategoryListResponseDto categories) {
		this.category = category;
		this.categories = categories;
	}
	
	public CategoryResponseDto getCategory() {
		return category;
	}
	
	public CategoryListResponseDto getCategories() {
		return categories;
	}
}
