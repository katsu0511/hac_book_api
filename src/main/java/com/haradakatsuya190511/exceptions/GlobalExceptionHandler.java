package com.haradakatsuya190511.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(LoginFailedException.class)
	public ResponseEntity<Map<String, String>> handleLoginFailed(LoginFailedException ex) {
		return ResponseEntity
			.status(HttpStatus.UNAUTHORIZED)
			.body(Map.of("loginFailed", ex.getMessage()));
	}
	
	@ExceptionHandler(SignupFailedException.class)
	public ResponseEntity<Map<String, String>> handleSignupFailed(SignupFailedException ex) {
		return ResponseEntity
			.status(HttpStatus.CONFLICT)
			.body(Map.of("signupFailed", ex.getMessage()));
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getFieldErrors().forEach(error ->
			errors.put(error.getField(), error.getDefaultMessage())
		);
		return ResponseEntity.badRequest().body(errors);
	}
}
