package com.huertos.parcels.domain;

import java.util.Locale;
import java.util.Optional;

public enum SoilType {
    ARCILLOSO,
    LIMOSO,
    ARENOSO;

    public String apiValue() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static Optional<SoilType> parse(String raw) {
        if (raw == null || raw.isBlank()) {
            return Optional.empty();
        }
        return switch (raw.trim().toLowerCase(Locale.ROOT)) {
            case "arcilloso" -> Optional.of(ARCILLOSO);
            case "limoso" -> Optional.of(LIMOSO);
            case "arenoso" -> Optional.of(ARENOSO);
            default -> Optional.empty();
        };
    }
}
