package com.haradakatsuya190511.controllers;

import java.security.Principal;
import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.haradakatsuya190511.dtos.summary.SummaryResponseDto;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.services.AuthService;
import com.haradakatsuya190511.services.DashboardService;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
	
	private final AuthService authService;
	private final DashboardService dashboardService;
	
	public DashboardController(AuthService authService, DashboardService dashboardService) {
		this.authService = authService;
		this.dashboardService = dashboardService;
	}
	
	@GetMapping("/summary")
	public ResponseEntity<SummaryResponseDto> getSummary(
			Principal principal,
			@RequestParam(value = "start", required = false) LocalDate start,
			@RequestParam(value = "end", required = false) LocalDate end
	) {
		User user = authService.getUser(principal);
		return ResponseEntity.ok(dashboardService.getSummary(user, start, end));
	}
}
