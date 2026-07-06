package com.haradakatsuya190511.exceptions;

public class OldPasswordNotMatchException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public OldPasswordNotMatchException() {
		super("Old password is not correct.");
	}
	
	public OldPasswordNotMatchException(String message) {
		super(message);
	}
}
