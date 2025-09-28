package com.haradakatsuya190511.dtos.shared;

import com.haradakatsuya190511.enums.CategoryType;

public interface CategoryRequest {
	Long getParentId();
	String getName();
	CategoryType getType();
	String getDescription();
}
