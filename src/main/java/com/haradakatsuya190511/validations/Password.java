package com.haradakatsuya190511.validations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {
	String message() default "Password must contain at least one uppercase letter, one lowercase letter, one number and one of them @$!%*?& and 8 to 64 letters";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
