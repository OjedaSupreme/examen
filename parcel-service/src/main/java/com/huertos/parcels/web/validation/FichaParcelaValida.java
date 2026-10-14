package com.huertos.parcels.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = FichaParcelaValidator.class)
@Target(TYPE)
@Retention(RUNTIME)
public @interface FichaParcelaValida {

    String message() default "La ficha de la parcela no se puede registrar.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
