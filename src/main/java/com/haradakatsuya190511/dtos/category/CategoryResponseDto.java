package com.haradakatsuya190511.dtos.category;

import com.haradakatsuya190511.entities.Category;
import com.haradakatsuya190511.enums.CategoryType;

public class CategoryResponseDto {
	private Long id;
	private Long userId;
	private Long parentId;
	private String name;
	private CategoryType type;
	private String description;
	private boolean isActive;
	
	public CategoryResponseDto(Category category) {
		this.id = category.getId();
		this.userId = category.getUser() == null ? null : category.getUser().getId();
		this.parentId = category.getParentCategory() == null ? null : category.getParentCategory().getId();
		this.name = category.getName();
		this.type = category.getType();
		this.description = category.getDescription();
		this.isActive = category.isActive();
	}
	
	public Long getId() {
		return id;
	}
	
	public Long getUserId() {
		return userId;
	}
	
	public Long getParentId() {
		return parentId;
	}
	
	public String getName() {
		return name;
	}
	
	public CategoryType getType() {
		return type;
	}
	
	public String getDescription() {
		return description;
	}
	
	public boolean isActive() {
		return isActive;
	}
}
