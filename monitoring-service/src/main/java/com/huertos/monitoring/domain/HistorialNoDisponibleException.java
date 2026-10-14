package com.huertos.monitoring.domain;

import org.springframework.http.HttpStatus;

public class HistorialNoDisponibleException extends ExcepcionDeMonitoreo {

    public HistorialNoDisponibleException(Throwable causa) {
        super(
                HttpStatus.SERVICE_UNAVAILABLE,
                "HISTORIAL_SENSORES_NO_DISPONIBLE",
                "El historial de sensores no está disponible",
                "La medición no se almacenó porque el almacén de lecturas no respondió. No es un problema del pH ni de la humedad: la cifra ni siquiera llegó a escribirse, para no dejar el estado de la parcela a medias.",
                causa
        );
    }
}
