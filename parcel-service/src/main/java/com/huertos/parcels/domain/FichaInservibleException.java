package com.huertos.parcels.domain;

import org.springframework.http.HttpStatus;

public class FichaInservibleException extends ExcepcionDeHuerto {

    public FichaInservibleException(String titulo, String explicacion) {
        super(HttpStatus.BAD_REQUEST, "FICHA_INSERVIBLE", titulo, explicacion);
    }
}
