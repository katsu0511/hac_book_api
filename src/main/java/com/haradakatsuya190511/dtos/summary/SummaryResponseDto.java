package com.haradakatsuya190511.dtos.summary;

import java.math.BigDecimal;
import java.util.List;

public class SummaryResponseDto {
	private BigDecimal expense;
	private BigDecimal income;
	private List<ExpenseBreakdownDto> expenseBreakdown;
	
	public SummaryResponseDto(BigDecimal expense, BigDecimal income, List<ExpenseBreakdownDto> expenseBreakdown) {
		this.expense = expense;
		this.income = income;
		this.expenseBreakdown = expenseBreakdown;
	}
	
	public BigDecimal getExpense() {
		return expense;
	}
	
	public BigDecimal getIncome() {
		return income;
	}
	
	public List<ExpenseBreakdownDto> getExpenseBreakdown() {
		return expenseBreakdown;
	}
}
