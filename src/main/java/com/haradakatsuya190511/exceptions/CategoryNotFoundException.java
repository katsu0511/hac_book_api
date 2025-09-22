package com.haradakatsuya190511.exceptions;

public class CategoryNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public CategoryNotFoundException() {
		super("category not found");
	}
	
	public CategoryNotFoundException(String message) {
		super(message);
	}
}
