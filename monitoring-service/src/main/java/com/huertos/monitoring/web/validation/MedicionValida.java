package com.huertos.monitoring.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = MedicionValidator.class)
@Target(TYPE)
@Retention(RUNTIME)
public @interface MedicionValida {

    String message() default "La medición del sensor no se puede guardar.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
