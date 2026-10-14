package com.huertos.monitoring.domain;

import org.springframework.http.HttpStatus;

public class MedicionInservibleException extends ExcepcionDeMonitoreo {

    public MedicionInservibleException(String titulo, String explicacion) {
        super(HttpStatus.BAD_REQUEST, "MEDICION_INSERVIBLE", titulo, explicacion);
    }
}
