package com.haradakatsuya190511.dtos.shared;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TransactionRequest {
	Long getUserId();
	Long getCategoryId();
	BigDecimal getAmount();
	String getCurrency();
	String getDescription();
	LocalDate getTransactionDate();
}
