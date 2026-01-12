package com.haradakatsuya190511.dtos.category;

import com.haradakatsuya190511.dtos.category.shared.CategoryRequest;
import com.haradakatsuya190511.enums.CategoryType;

public class UpdateCategoryRequestDto implements CategoryRequest {
	private Long id;
	private Long parentId;
	private String name;
	private CategoryType type;
	private String description;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getParentId() {
		return parentId;
	}
	
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public CategoryType getType() {
		return type;
	}
	
	public void setType(CategoryType type) {
		this.type = type;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
}
