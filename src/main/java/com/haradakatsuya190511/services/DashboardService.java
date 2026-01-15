package com.haradakatsuya190511.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.haradakatsuya190511.dtos.summary.ExpenseBreakdownDto;
import com.haradakatsuya190511.dtos.summary.SummaryResponseDto;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.enums.CategoryType;
import com.haradakatsuya190511.repositories.DashboardRepository;

@Service
public class DashboardService {
	
	private final DashboardRepository dashboardRepository;
	
	public DashboardService(DashboardRepository dashboardRepository) {
		this.dashboardRepository = dashboardRepository;
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
		return dashboardRepository.findSumByCategoryTypeInPeriod(user, CategoryType.EXPENSE, start, end);
	}
	
	private BigDecimal getTotalIncome(User user, LocalDate start, LocalDate end) {
		return dashboardRepository.findSumByCategoryTypeInPeriod(user, CategoryType.INCOME, start, end);
	}
	
	public List<ExpenseBreakdownDto> getExpenseBreakdown(User user, LocalDate start, LocalDate end) {
		List<ExpenseBreakdownDto> dtos = dashboardRepository.findBreakdownByCategoryType(user, CategoryType.EXPENSE, start, end);
		return getBreakdown(dtos);
	}
	
	private List<ExpenseBreakdownDto> getBreakdown(List<ExpenseBreakdownDto> dtos) {
		Map<Long, List<ExpenseBreakdownDto>> childrenMap = new HashMap<>();
		List<ExpenseBreakdownDto> parents = new ArrayList<>();
		ExpenseBreakdownDto others = null;
		
		for (ExpenseBreakdownDto dto : dtos) {			
			if ("Others".equals(dto.getCategoryName())) others = dto;
	        else if (dto.getParentId() == null) parents.add(dto);
	        else childrenMap.computeIfAbsent(dto.getParentId(), k -> new ArrayList<>()).add(dto);
		}
		
		List<ExpenseBreakdownDto> result = new ArrayList<>();
		for (ExpenseBreakdownDto parent : parents) {
			result.add(parent);
			result.addAll(childrenMap.getOrDefault(parent.getCategoryId(), List.of()));
		}
		
		if (others != null) result.add(others);
		
		return result.stream()
			.filter(dto -> dto.getTotal() != null)
			.filter(dto -> dto.getTotal().compareTo(BigDecimal.ZERO) != 0)
			.toList();
	}
}
