package com.haradakatsuya190511.dtos.transaction;

import com.haradakatsuya190511.dtos.category.CategoryListResponseDto;

public class TransactionForEditResponseDto {
	private TransactionResponseDto transaction;
	private CategoryListResponseDto categories;
	
	public TransactionForEditResponseDto(TransactionResponseDto transaction, CategoryListResponseDto categories) {
		this.transaction = transaction;
		this.categories = categories;
	}
	
	public TransactionResponseDto getTransaction() {
		return transaction;
	}
	
	public CategoryListResponseDto getCategories() {
		return categories;
	}
}
