package com.haradakatsuya190511.dtos.profile;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class UpdateMonthlySavingGoalRequestDto {
	
	@NotNull
	@Positive
	@Digits(integer = 8, fraction = 2)
	private BigDecimal monthlySavingGoal;
	
	public BigDecimal getMonthlySavingGoal() {
		return monthlySavingGoal;
	}
	
	public void setMonthlySavingGoal(BigDecimal monthlySavingGoal) {
		this.monthlySavingGoal = monthlySavingGoal;
	}
}
