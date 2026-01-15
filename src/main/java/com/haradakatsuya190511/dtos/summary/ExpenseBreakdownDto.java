package com.haradakatsuya190511.dtos.summary;

import java.math.BigDecimal;

public class ExpenseBreakdownDto {
	private Long categoryId;
	private String categoryName;
	private Long parentId;
	private BigDecimal total;
	
	public ExpenseBreakdownDto(Long categoryId, String categoryName, Long parentId, BigDecimal total) {
		this.categoryId = categoryId;
		this.categoryName = categoryName;
		this.parentId = parentId;
		this.total = total;
	}
	
	public Long getCategoryId() {
		return categoryId;
	}
	
	public String getCategoryName() {
		return categoryName;
	}
	
	public Long getParentId() {
		return parentId;
	}
	
	public BigDecimal getTotal() {
		return total;
	}
}
