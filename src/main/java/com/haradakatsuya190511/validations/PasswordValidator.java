package com.haradakatsuya190511.validations;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String> {
	
	private static final Pattern PASSWORD_PATTERN = Pattern.compile(
		"^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[a-zA-Z\\d@$!%*?&]{8,64}$"
	);
	
	@Override
	public boolean isValid(String password, ConstraintValidatorContext context) {
		return password != null && PASSWORD_PATTERN.matcher(password).matches();
	}
}
