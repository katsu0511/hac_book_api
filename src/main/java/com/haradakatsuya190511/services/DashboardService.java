package com.haradakatsuya190511.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;

import com.haradakatsuya190511.dtos.category.CategoryResponseDto;
import com.haradakatsuya190511.dtos.summary.ExpenseBreakdownDto;
import com.haradakatsuya190511.dtos.summary.SummaryResponseDto;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.enums.CategoryType;
import com.haradakatsuya190511.repositories.TransactionRepository;

@Service
public class DashboardService {
	
	private final TransactionRepository transactionRepository;
	private final CategoryService categoryService;
	
	public DashboardService(TransactionRepository transactionRepository, CategoryService categoryService) {
		this.transactionRepository = transactionRepository;
		this.categoryService = categoryService;
	}
	
	public SummaryResponseDto getSummary(User user, LocalDate start, LocalDate end) {
		if (start == null || end == null) {
			YearMonth thisMonth = YearMonth.now();
			start = thisMonth.atDay(1);
			end = thisMonth.atEndOfMonth();
		}
		return new SummaryResponseDto(getTotalExpense(user, start, end), getTotalIncome(user, start, end), getExpenseBreakdown(user, start, end));
	}
	
	private BigDecimal getTotalExpense(User user, LocalDate start, LocalDate end) {
		return transactionRepository.findSumByCategoryTypeAndPeriod(user, CategoryType.EXPENSE, start, end);
	}
	
	private BigDecimal getTotalIncome(User user, LocalDate start, LocalDate end) {
		return transactionRepository.findSumByCategoryTypeAndPeriod(user, CategoryType.INCOME, start, end);
	}
	
	public List<ExpenseBreakdownDto> getExpenseBreakdown(User user, LocalDate start, LocalDate end) {
		List<CategoryResponseDto> categories = categoryService.getExpenseCategories(user);
		return getBreakdown(categories, user, start, end);
	}
	
	private List<ExpenseBreakdownDto> getBreakdown(List<CategoryResponseDto> categories, User user, LocalDate start, LocalDate end) {
		return categories.stream()
			.map(c -> new ExpenseBreakdownDto(
				c.getId(),
				c.getName(),
				transactionRepository.findSumByCategoryAndPeriod(user, c.getId(), start, end)
			))
			.filter(dto ->
				dto.getTotal() != null &&
				dto.getTotal().compareTo(BigDecimal.ZERO) > 0
			)
			.toList();
	}
}
