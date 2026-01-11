package com.haradakatsuya190511.exceptions;

public class EmailAlreadyUsedException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public EmailAlreadyUsedException() {
		super("Email already used.");
	}
	
	public EmailAlreadyUsedException(String message) {
		super(message);
	}
}
