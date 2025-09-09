package com.haradakatsuya190511.exceptions;

public class SignupFailedException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public SignupFailedException() {
		super("user already exists");
	}
	
	public SignupFailedException(String message) {
		super(message);
	}
}
