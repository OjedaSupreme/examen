// 3. Constraints

package com.cmp_student.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SanitizedHtmlValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface SanitizedHtml {
    String message() default "El texto contiene contenido HTML no permitido o malicioso";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

class SanitizedHtmlValidator implements ConstraintValidator<SanitizedHtml, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Los nulos se manejan con @NotNull o @NotBlank por separado
        }

        // Sanitizamos el texto usando una lista blanca estricta (ej. sin etiquetas o
        // solo básicas)
        // Safelist.basic() permite etiquetas formateadas básicas; Safelist.none()
        // remueve todo HTML.
        String sanitizedValue = Jsoup.clean(value, Safelist.basic());

        // Opcional: Si quieres rechazar el input en lugar de limpiarlo silenciosamente,
        // puedes comparar si el texto original era diferente al sanitizado.
        return value.equals(sanitizedValue);
    }
}