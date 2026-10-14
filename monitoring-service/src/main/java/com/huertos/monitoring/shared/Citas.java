package com.huertos.monitoring.shared;

import java.text.Normalizer;

public final class Citas {

    private Citas() {
    }

    public static String entreComillas(String value) {
        if (value == null || value.isBlank()) {
            return "vacío";
        }
        String limpio = Normalizer.normalize(value, Normalizer.Form.NFKC)
                .replaceAll("[\\p{Cc}\\p{Cf}<>{}$]", "")
                .trim()
                .replaceAll("\\s+", " ");
        if (limpio.isEmpty()) {
            return "un texto que solo traía caracteres de control";
        }
        if (limpio.length() > 60) {
            limpio = limpio.substring(0, 60) + "…";
        }
        return "«" + limpio + "»";
    }

    public static String numero(double valor) {
        return java.math.BigDecimal.valueOf(valor).stripTrailingZeros().toPlainString();
    }
}
