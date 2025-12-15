package com.haradakatsuya190511.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
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
	
	public String getTotalExpense(User user, LocalDate start, LocalDate end) {
		return transactionRepository.getTotalExpenseInPeriod(user, start, end);
	}
	
	public String getTotalIncome(User user, LocalDate start, LocalDate end) {
		return transactionRepository.getTotalIncomeInPeriod(user, start, end);
	}
	
	public Map<String, String> getExpenseBreakdown(User user, LocalDate start, LocalDate end) {
		List<CategoryResponseDto> defaultCategories = categoryService.getDefaultExpenseCategories();
		Map<String, String> map = getBreakdown(defaultCategories, user, start, end);
		BigDecimal others = transactionRepository.getTotalExpenseByUserCategoryInPeriod(user, start, end);
		BigDecimal existingOthers = new BigDecimal(map.getOrDefault("Others", "0.00"));
		map.put("Others", existingOthers.add(others).toString());
		return map;
	}
	
	public Map<String, String> getIncomeBreakdown(User user, LocalDate start, LocalDate end) {
		List<CategoryResponseDto> defaultCategories = categoryService.getDefaultIncomeCategories();
		Map<String, String> map = getBreakdown(defaultCategories, user, start, end);
		BigDecimal others = transactionRepository.getTotalIncomeByUserCategoryInPeriod(user, start, end);
		BigDecimal existingOthers = new BigDecimal(map.getOrDefault("Others", "0.00"));
		map.put("Others", existingOthers.add(others).toString());
		return map;
	}
	
	private Map<String, String> getBreakdown(List<CategoryResponseDto> categories, User user, LocalDate start, LocalDate end) {
		return categories.stream().collect(Collectors.toMap(
			CategoryResponseDto::getName,
			c -> transactionRepository.findSumByCategoryAndPeriod(user, c.getId(), start, end),
			(a, b) -> a,
			LinkedHashMap::new
		));
	}
}
