package com.huertos.monitoring.web.error;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RespuestaError(
        Instant ocurridoEn,
        int estado,
        String codigo,
        String titulo,
        String explicacion,
        String ruta,
        List<FalloCampo> fallos
) {
}
