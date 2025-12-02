package com.haradakatsuya190511.controllers;

import java.security.Principal;
import java.time.LocalDate;
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
			@RequestParam("start") LocalDate start,
			@RequestParam("end") LocalDate end,
			Principal principal
	) {
		User user = authService.getUser(principal);
		return ResponseEntity.ok(
			Map.of(
				"income", dashboardService.getTotalIncome(user, start, end),
				"expense", dashboardService.getTotalExpense(user, start, end),
				"incomeBreakdown", dashboardService.getIncomeBreakdown(user, start, end),
				"expenseBreakdown", dashboardService.getExpenseBreakdown(user, start, end)
			)
		);
	}
}
