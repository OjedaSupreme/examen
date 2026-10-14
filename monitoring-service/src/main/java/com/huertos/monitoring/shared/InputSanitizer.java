package com.huertos.monitoring.shared;

import com.huertos.monitoring.domain.ContenidoNoAdmitidoException;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.regex.Pattern;

@Component
public class InputSanitizer {

    private static final Pattern ETIQUETAS = Pattern.compile("<\\s*/?\\s*[a-zA-Z][^>]*>");
    private static final Pattern CONTROL = Pattern.compile("[\\p{Cc}\\p{Cf}]");
    private static final Pattern BLOQUEADO = Pattern.compile(
            "(?i)(<\\s*/?\\s*[a-zA-Z]|javascript\\s*:|vbscript\\s*:|on(?:error|load|click)\\s*=|\\$(?:where|ne|gt|gte|lt|lte|eq|regex|or|and|expr)|\\{\\s*\\$)"
    );
    private static final Pattern UUID = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    );

    public String sanitize(String value) {
        if (value == null) {
            return null;
        }
        String normalizado = Normalizer.normalize(value, Normalizer.Form.NFKC);
        String sinEtiquetas = ETIQUETAS.matcher(normalizado).replaceAll("");
        String sinControl = CONTROL.matcher(sinEtiquetas).replaceAll("");
        return sinControl.trim().replaceAll("\\s+", " ");
    }

    public boolean enBlanco(String value) {
        String limpio = sanitize(value);
        return limpio == null || limpio.isBlank();
    }

    public boolean contieneContenidoBloqueado(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        return BLOQUEADO.matcher(value).find() || ETIQUETAS.matcher(value).find();
    }

    public void assertLimpio(String dato, String value) {
        if (contieneContenidoBloqueado(value)) {
            throw new ContenidoNoAdmitidoException(dato);
        }
    }

    public boolean esUuid(String value) {
        return value != null && UUID.matcher(value).matches();
    }
}
