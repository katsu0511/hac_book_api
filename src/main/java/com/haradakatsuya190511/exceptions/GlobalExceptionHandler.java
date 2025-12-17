package com.haradakatsuya190511.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<Map<String, String>> handleUsernameNotFound(UsernameNotFoundException ex) {
		return ResponseEntity.badRequest().body(Map.of("notFound", ex.getMessage()));
	}
	
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
	
	@ExceptionHandler(CategoryNotFoundException.class)
	public ResponseEntity<Map<String, String>> handleCategoryNotFound(CategoryNotFoundException ex) {
		return ResponseEntity.badRequest().body(Map.of("notFound", ex.getMessage()));
	}
	
	@ExceptionHandler(InvalidParentCategoryException.class)
	public ResponseEntity<Map<String, String>> handleInvalidParentCategory(InvalidParentCategoryException ex) {
		return ResponseEntity.badRequest().body(Map.of("invalid", ex.getMessage()));
	}
	
	@ExceptionHandler(TransactionNotFoundException.class)
	public ResponseEntity<Map<String, String>> handleTransactionNotFound(TransactionNotFoundException ex) {
		return ResponseEntity.badRequest().body(Map.of("notFound", ex.getMessage()));
	}
}
