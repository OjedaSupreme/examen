package com.huertos.monitoring.domain;

import org.springframework.http.HttpStatus;

public class ServicioParcelasNoDisponibleException extends ExcepcionDeMonitoreo {

    public ServicioParcelasNoDisponibleException(Throwable causa) {
        super(
                HttpStatus.SERVICE_UNAVAILABLE,
                "SERVICIO_PARCELAS_NO_RESPONDE",
                "No se pudo confirmar que la parcela exista",
                "La lectura no se guardó: el servicio de parcelas no respondió y, sin confirmar que la zona está dada de alta, el monitoreo no deja una medición colgando de un identificador sin ficha.",
                causa
        );
    }
}
