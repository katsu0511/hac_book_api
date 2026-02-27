package com.haradakatsuya190511.validations;

import static org.assertj.core.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class PasswordValidationTest {
	
	private static ValidatorFactory factory;
	private static Validator validator;
	
	@BeforeAll
	static void setup() {
		factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}
	
	@AfterAll
	static void tearDown() {
		factory.close();
	}
	
	static class Req {
		@Password
		private final String password;
		
		Req(String password) {
			this.password = password;
		}
	}
	
	private Set<ConstraintViolation<Req>> validate(String password) {
		return validator.validate(new Req(password));
	}
	
	@Test
	void validPassword_passes() {
		assertThat(validate("Abcdef1@")).isEmpty();
		assertThat(validate("Zz9$aaaa")).isEmpty();
		assertThat(validate("A1b2c3d4!")).isEmpty();
	}
	
	@Test
	void null_fails() {
		Set<ConstraintViolation<Req>> v = validate(null);
		assertThat(v).hasSize(1);
		
		ConstraintViolation<Req> violation = v.iterator().next();
		assertThat(violation.getPropertyPath().toString()).isEqualTo("password");
		assertThat(violation.getMessage()).isEqualTo(
			"Password must contain at least one uppercase letter, one lowercase letter, one number and one of them @$!%*?& and 8 to 64 letters"
		);
	}
	
	@Test
	void tooShort_fails() {
		assertThat(validate("")).isNotEmpty();
		assertThat(validate("aA1@")).isNotEmpty();
		assertThat(validate("Ab1@aaa")).isNotEmpty();
	}
	
	@Test
	void tooLong_fails() {
		String longPwd = "Ab1@" + "a".repeat(61);
		assertThat(validate(longPwd)).isNotEmpty();
	}
	
	@Test
	void missingLowercase_fails() {
		assertThat(validate("ABCDEFG1@")).isNotEmpty();
	}
	
	@Test
	void missingUppercase_fails() {
		assertThat(validate("abcdefg1@")).isNotEmpty();
	}
	
	@Test
	void missingDigit_fails() {
		assertThat(validate("Abcdefgh@")).isNotEmpty();
	}
	
	@Test
	void missingSpecial_fails() {
		assertThat(validate("Abcdefg1")).isNotEmpty();
	}
	
	@Test
	void invalidSpecialCharacter_fails() {
		assertThat(validate("Abcdef1#")).isNotEmpty();
	}
	
	@Test
	void whitespace_fails() {
		assertThat(validate("Abcdef1@ ")).isNotEmpty();
		assertThat(validate(" Abcdef1@")).isNotEmpty();
	}
}
