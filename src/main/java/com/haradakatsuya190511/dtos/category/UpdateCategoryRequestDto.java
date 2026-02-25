package com.haradakatsuya190511.dtos.category;

import com.haradakatsuya190511.dtos.category.shared.CategoryRequest;
import com.haradakatsuya190511.enums.CategoryType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdateCategoryRequestDto implements CategoryRequest {
	
	@NotNull
	private Long id;
	
	@NotNull
	private Long userId;
	
	private Long parentId;
	
	@NotBlank
	@Size(max = 100)
	private String name;
	
	@NotNull
	private CategoryType type;
	
	@Size(max = 200)
	private String description;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getUserId() {
		return userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
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
