package com.haradakatsuya190511.exceptions;

public class InvalidParentCategoryException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public InvalidParentCategoryException() {
		super("Invalid parent category");
	}
	
	public InvalidParentCategoryException(String message) {
		super(message);
	}
}
