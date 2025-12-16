package com.haradakatsuya190511.controllers;

import java.security.Principal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.services.AuthService;
import com.haradakatsuya190511.services.DashboardService;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
	
	@Autowired
	AuthService authService;
	
	@Autowired
	DashboardService dashboardService;
	
	@GetMapping("/summary")
	public ResponseEntity<Map<String, Object>> getSummary(
			@RequestParam(value = "start", required = false) LocalDate start,
			@RequestParam(value = "end", required = false) LocalDate end,
			Principal principal
	) {
		User user = authService.getUser(principal);
		if (start == null || end == null) {
			YearMonth thisMonth = YearMonth.now();
			start = thisMonth.atDay(1);
			end = thisMonth.atEndOfMonth();
		}
		return ResponseEntity.ok(
			Map.of(
				"expense", dashboardService.getTotalExpense(user, start, end),
				"income", dashboardService.getTotalIncome(user, start, end),
				"expenseBreakdown", dashboardService.getExpenseBreakdown(user, start, end),
				"incomeBreakdown", dashboardService.getIncomeBreakdown(user, start, end)
			)
		);
	}
}
