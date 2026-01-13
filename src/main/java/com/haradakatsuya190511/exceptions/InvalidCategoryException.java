package com.haradakatsuya190511.exceptions;

public class InvalidCategoryException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public InvalidCategoryException() {
		super("Invalid category.");
	}
	
	public InvalidCategoryException(String message) {
		super(message);
	}
}
