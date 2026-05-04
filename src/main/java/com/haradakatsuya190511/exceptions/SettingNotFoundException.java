package com.haradakatsuya190511.exceptions;

public class SettingNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public SettingNotFoundException() {
		super("Setting not found.");
	}
	
	public SettingNotFoundException(String message) {
		super(message);
	}
}
