package com.haradakatsuya190511.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.haradakatsuya190511.dtos.CategoryResponseDto;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.repositories.TransactionRepository;

@Service
public class DashboardService {
	
	@Autowired
	TransactionRepository transactionRepository;
	
	@Autowired
	CategoryService categoryService;
	
	public String getTotalIncome(User user, LocalDate start, LocalDate end) {
		return transactionRepository.getTotalIncomeInPeriod(user, start, end);
	}
	
	public String getTotalExpense(User user, LocalDate start, LocalDate end) {
		return transactionRepository.getTotalExpenseInPeriod(user, start, end);
	}
	
	public Map<String, String> getIncomeBreakdown(User user, LocalDate start, LocalDate end) {
		List<CategoryResponseDto> categories = categoryService.getIncomeCategories(user);
		
		return categories.stream()
			.collect(Collectors.toMap(
				CategoryResponseDto::getName,
				c -> transactionRepository.findSumByUserAndCategoryAndPeriod(
					user, c.getId(), start, end
				)
			));
	}
	
	public Map<String, String> getExpenseBreakdown(User user, LocalDate start, LocalDate end) {
		List<CategoryResponseDto> categories = categoryService.getExpenseCategories(user);
		
		return categories.stream()
			.collect(Collectors.toMap(
				CategoryResponseDto::getName,
				c -> transactionRepository.findSumByUserAndCategoryAndPeriod(
					user, c.getId(), start, end
				)
			));
	}
}
