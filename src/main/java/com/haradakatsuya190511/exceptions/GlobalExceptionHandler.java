package com.haradakatsuya190511.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.haradakatsuya190511.dtos.error.ErrorResponseDto;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<ErrorResponseDto> handleUsernameNotFound(UsernameNotFoundException ex) {
		return ResponseEntity.badRequest().body(new ErrorResponseDto(ex.getMessage()));
	}
	
	@ExceptionHandler(LoginFailedException.class)
	public ResponseEntity<ErrorResponseDto> handleLoginFailed(LoginFailedException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDto(ex.getMessage()));
	}
	
	@ExceptionHandler(EmailAlreadyUsedException.class)
	public ResponseEntity<ErrorResponseDto> handleUserAlreadyExisted(EmailAlreadyUsedException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDto(ex.getMessage()));
	}
	
	@ExceptionHandler(CategoryNotFoundException.class)
	public ResponseEntity<ErrorResponseDto> handleCategoryNotFound(CategoryNotFoundException ex) {
		return ResponseEntity.badRequest().body(new ErrorResponseDto(ex.getMessage()));
	}
	
	@ExceptionHandler(InvalidParentCategoryException.class)
	public ResponseEntity<ErrorResponseDto> handleInvalidParentCategory(InvalidParentCategoryException ex) {
		return ResponseEntity.badRequest().body(new ErrorResponseDto(ex.getMessage()));
	}
	
	@ExceptionHandler(TransactionNotFoundException.class)
	public ResponseEntity<ErrorResponseDto> handleTransactionNotFound(TransactionNotFoundException ex) {
		return ResponseEntity.badRequest().body(new ErrorResponseDto(ex.getMessage()));
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
