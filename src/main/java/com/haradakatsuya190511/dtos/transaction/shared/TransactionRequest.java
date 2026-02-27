package com.haradakatsuya190511.dtos.transaction.shared;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TransactionRequest {
	Long getCategoryId();
	BigDecimal getAmount();
	String getCurrency();
	String getDescription();
	LocalDate getTransactionDate();
}
