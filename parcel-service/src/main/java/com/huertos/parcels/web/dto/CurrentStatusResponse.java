package com.huertos.parcels.web.dto;

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

    public static CurrentStatusResponse sinLectura() {
        return new CurrentStatusResponse(
                false,
                "SIN_LECTURA_DE_SENSOR",
                "Esta parcela todavía no tiene una medición de suelo o aire. La zona ya puede estar en el catálogo, pero humedad, temperatura, pH, nutrientes y calidad del aire aparecen cuando el sensor manda la primera lectura.",
                null, null, null, null, null, null, null, null
        );
    }

    public static CurrentStatusResponse monitoreoCaido() {
        return new CurrentStatusResponse(
                false,
                "MONITOREO_SIN_RESPUESTA",
                "La parcela sí está en el catálogo, pero el servicio de monitoreo no respondió. No se muestran humedad, pH ni calidad del aire para no inventar el estado de la zona.",
                null, null, null, null, null, null, null, null
        );
    }
}
