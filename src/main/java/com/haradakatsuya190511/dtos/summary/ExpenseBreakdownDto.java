package com.haradakatsuya190511.dtos.summary;

import java.math.BigDecimal;

public class ExpenseBreakdownDto {
	private Long categoryId;
	private String categoryName;
	private BigDecimal total;
	
	public ExpenseBreakdownDto(Long categoryId, String categoryName, BigDecimal total) {
		this.categoryId = categoryId;
		this.categoryName = categoryName;
		this.total = total;
	}
	
	public Long getCategoryId() {
		return categoryId;
	}
	
	public String getCategoryName() {
		return categoryName;
	}
	
	public BigDecimal getTotal() {
		return total;
	}
}
