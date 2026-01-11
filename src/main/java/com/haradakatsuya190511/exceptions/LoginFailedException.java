package com.haradakatsuya190511.exceptions;

public class LoginFailedException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public LoginFailedException() {
		super("Failed to login.");
	}
	
	public LoginFailedException(String message) {
		super(message);
	}
}
