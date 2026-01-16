package com.haradakatsuya190511.controllers;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.haradakatsuya190511.dtos.summary.SummaryResponseDto;
import com.haradakatsuya190511.entities.User;
import com.haradakatsuya190511.services.DashboardService;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
	
	private final DashboardService dashboardService;
	
	public DashboardController(DashboardService dashboardService) {
		this.dashboardService = dashboardService;
	}
	
	@GetMapping("/summary")
	public ResponseEntity<SummaryResponseDto> getSummary(
		@AuthenticationPrincipal User user,
		@RequestParam(value = "start", required = false) LocalDate start,
		@RequestParam(value = "end", required = false) LocalDate end
	) {
		return ResponseEntity.ok(dashboardService.getSummary(user, start, end));
	}
}
