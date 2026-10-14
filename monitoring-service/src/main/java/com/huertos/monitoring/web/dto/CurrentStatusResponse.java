package com.huertos.monitoring.web.dto;

import java.time.Instant;

public record CurrentStatusResponse(
        boolean available,
        String situation,
        String summary,
        Double soilMoisture,
        Double temperature,
        Double ph,
        Double nitrogen,
        Double phosphorus,
        Double potassium,
        Double airQualityIndex,
        Instant recordedAt
) {

    public static CurrentStatusResponse sinLectura(String parcelId) {
        String cita = parcelId == null || parcelId.isBlank() ? "esta parcela" : "la parcela " + parcelId;
        return new CurrentStatusResponse(
                false,
                "SIN_LECTURA_DE_SENSOR",
                "Todavía no hay una medición ligada a " + cita
                        + ". Humedad, temperatura, pH, nitrógeno, fósforo, potasio y calidad del aire aparecerán cuando el sensor mande la primera lectura; hasta entonces la zona no está diagnosticada.",
                null, null, null, null, null, null, null, null
        );
    }
}
