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
	
	private static final String OTHERS = "Others";
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
	
	public List<ExpenseBreakdownDto> getExpenseBreakdown(User user, LocalDate start, LocalDate end) {
		List<Object[]> rows = dashboardRepository.findBreakdownByCategoryType(user, CategoryType.EXPENSE, start, end);
		return getBreakdown(rows);
	}
	
	private BigDecimal getTotalExpense(User user, LocalDate start, LocalDate end) {
		return dashboardRepository.findSumByCategoryTypeInPeriod(user, CategoryType.EXPENSE, start, end);
	}
	
	private BigDecimal getTotalIncome(User user, LocalDate start, LocalDate end) {
		return dashboardRepository.findSumByCategoryTypeInPeriod(user, CategoryType.INCOME, start, end);
	}
	
	private List<ExpenseBreakdownDto> getBreakdown(List<Object[]> rows) {
		Map<Long, List<ExpenseBreakdownDto>> childrenMap = new HashMap<>();
		List<ExpenseBreakdownDto> parents = new ArrayList<>();
		ExpenseBreakdownDto others = null;
		
		for (Object[] row : rows) {		
			Long id = (Long) row[0];
			String name = (String) row[1];
			Long parentId = (Long) row[2];
			BigDecimal sum = (BigDecimal) row[3];
			ExpenseBreakdownDto dto = new ExpenseBreakdownDto(id, name, parentId, sum);
			if (dto.getCategoryName().equals(OTHERS)) others = dto;
	        else if (dto.getParentId() == null) parents.add(dto);
	        else childrenMap.computeIfAbsent(dto.getParentId(), k -> new ArrayList<>()).add(dto);
		}
		
		return flattenCategories(parents, childrenMap, others);
	}
	
	private List<ExpenseBreakdownDto> flattenCategories(
		List<ExpenseBreakdownDto> parents,
		Map<Long, List<ExpenseBreakdownDto>> childrenMap,
		ExpenseBreakdownDto others
	) {
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
