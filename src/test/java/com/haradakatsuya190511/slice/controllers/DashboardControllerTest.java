package com.haradakatsuya190511.slice.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.haradakatsuya190511.controllers.DashboardController;
import com.haradakatsuya190511.dtos.summary.SummaryResponseDto;
import com.haradakatsuya190511.services.DashboardService;

@WebMvcTest(DashboardController.class)
@Import(AuthControllerTest.TestSecurityConfig.class)
class DashboardControllerTest {
	
	@Autowired
	MockMvc mockMvc;
	
	@MockitoBean
	DashboardService dashboardService;
	
	@Test
	@WithMockUser
	void getSummary_succeedAndShouldReturn200_whenNoParams() throws Exception {
		SummaryResponseDto dto = new SummaryResponseDto(new BigDecimal("1000"), new BigDecimal("500"), List.of());
		
		given(dashboardService.getSummary(any(), isNull(), isNull())).willReturn(dto);
		
		mockMvc.perform(get("/dashboard/summary"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.expense").value(1000))
			.andExpect(jsonPath("$.income").value(500))
			.andExpect(jsonPath("$.expenseBreakdown").isArray());
		
		verify(dashboardService).getSummary(any(), isNull(), isNull());
	}
	
	@Test
	@WithMockUser
	void getSummary_succeedAndShouldReturn200_whenWithParams() throws Exception {
		SummaryResponseDto dto = new SummaryResponseDto(new BigDecimal("1000"), new BigDecimal("500"), List.of());
		
		given(dashboardService.getSummary(any(), eq(LocalDate.of(2026,1,1)), eq(LocalDate.of(2026,1,31)))).willReturn(dto);
		
		mockMvc.perform(get("/dashboard/summary")
			.param("start", "2026-01-01")
			.param("end", "2026-01-31"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.expense").value(1000))
			.andExpect(jsonPath("$.income").value(500))
			.andExpect(jsonPath("$.expenseBreakdown").isArray());
		
		verify(dashboardService).getSummary(any(), eq(LocalDate.of(2026,1,1)), eq(LocalDate.of(2026,1,31)));
	}
	
	@Test
	@WithMockUser
	void getSummary_failAndShouldReturn400_whenInvalidDate() throws Exception {
		mockMvc.perform(get("/dashboard/summary")
			.param("start", "invalid")
			.param("end", "invalid"))
			.andExpect(status().isBadRequest());
		
		verify(dashboardService, never()).getSummary(any(), any(), any());
	}
	
	@Test
	void getSummary_failAndShouldReturn403_whenUnauthenticated() throws Exception {
		mockMvc.perform(get("/dashboard/summary")).andExpect(status().isForbidden());
		verify(dashboardService, never()).getSummary(any(), any(), any());
	}
}
