package com.haradakatsuya190511.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.haradakatsuya190511.dtos.summary.ExpenseBreakdownDto;
import com.haradakatsuya190511.dtos.summary.SummaryResponseDto;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.enums.CategoryType;
import com.haradakatsuya190511.repositories.DashboardRepository;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {
	
	@Mock
	DashboardRepository dashboardRepository;
	
	@InjectMocks
	DashboardService dashboardService;
	
	@Test
	void getSummary_success() {
		User user = new User();
		user.setId(1L);
		
		LocalDate start = LocalDate.of(2024, 1, 1);
		LocalDate end = LocalDate.of(2024, 1, 31);
		
		when(dashboardRepository.findSumByCategoryTypeInPeriod(
			eq(user), eq(CategoryType.EXPENSE), eq(start), eq(end)
		)).thenReturn(BigDecimal.valueOf(100));
		
		when(dashboardRepository.findSumByCategoryTypeInPeriod(
			eq(user), eq(CategoryType.INCOME), eq(start), eq(end)
		)).thenReturn(BigDecimal.valueOf(300));
		
		when(dashboardRepository.findBreakdownByCategoryType(
			eq(user), eq(CategoryType.EXPENSE), eq(start), eq(end)
		)).thenReturn(List.of());
		
		SummaryResponseDto dto = dashboardService.getSummary(user, start, end);
		
		assertEquals(BigDecimal.valueOf(100), dto.getExpense());
		assertEquals(BigDecimal.valueOf(300), dto.getIncome());
		assertTrue(dto.getExpenseBreakdown().isEmpty());
	}
	
	@Test
	void getSummary_whenPeriodIsNull_usesCurrentMonth() {
		User user = new User();
		user.setId(1L);
		
		when(dashboardRepository.findSumByCategoryTypeInPeriod(
			eq(user), any(), any(), any()
		)).thenReturn(BigDecimal.ZERO);
		
		when(dashboardRepository.findBreakdownByCategoryType(
			eq(user), eq(CategoryType.EXPENSE), any(), any()
		)).thenReturn(List.of());
		
		assertDoesNotThrow(() ->
			dashboardService.getSummary(user, null, null)
		);
	}
	
	@Test
	void getExpenseBreakdown_parentChildAndOthers_sortedCorrectly() {
		User user = new User();
		user.setId(1L);
		
		List<Object[]> rows = List.of(
			new Object[] {1L, "Others", null, BigDecimal.valueOf(20)},
			new Object[] {2L, "Dining", 4L, BigDecimal.valueOf(40)},
			new Object[] {3L, "Groceries", 4L, BigDecimal.valueOf(60)},
			new Object[] {4L, "Food", null, BigDecimal.valueOf(100)}
	    );
		
		when(dashboardRepository.findBreakdownByCategoryType(
			eq(user), eq(CategoryType.EXPENSE), any(), any()
		)).thenReturn(rows);
		
		List<ExpenseBreakdownDto> result = dashboardService.getExpenseBreakdown(user, LocalDate.now(), LocalDate.now());
		
		assertEquals(4, result.size());
		assertEquals("Food", result.get(0).getCategoryName());
		assertEquals("Dining", result.get(1).getCategoryName());
		assertEquals("Groceries", result.get(2).getCategoryName());
		assertEquals("Others", result.get(3).getCategoryName());
	}
	
	@Test
	void getExpenseBreakdown_filtersZeroAndNullTotals() {
		User user = new User();
		user.setId(1L);
		
		List<Object[]> rows = List.of(
			new Object[] {1L, "Food", null, BigDecimal.ZERO},
			new Object[] {2L, "Dining", null, null},
			new Object[] {3L, "Others", null, BigDecimal.valueOf(10)}
		);
		
		when(dashboardRepository.findBreakdownByCategoryType(
			eq(user), eq(CategoryType.EXPENSE), any(), any()
		)).thenReturn(rows);
		
		List<ExpenseBreakdownDto> result = dashboardService.getExpenseBreakdown(user, LocalDate.now(), LocalDate.now());
		
		assertEquals(1, result.size());
		assertEquals("Others", result.get(0).getCategoryName());
	}
}
