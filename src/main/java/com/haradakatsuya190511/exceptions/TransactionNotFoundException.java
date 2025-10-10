package com.haradakatsuya190511.exceptions;

public class TransactionNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public TransactionNotFoundException() {
		super("transaction not found");
	}
	
	public TransactionNotFoundException(String message) {
		super(message);
	}
}
