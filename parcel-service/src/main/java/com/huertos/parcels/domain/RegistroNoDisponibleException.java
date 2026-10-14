package com.huertos.parcels.domain;

import org.springframework.http.HttpStatus;

public class RegistroNoDisponibleException extends ExcepcionDeHuerto {

    public RegistroNoDisponibleException(Throwable causa) {
        super(
                HttpStatus.SERVICE_UNAVAILABLE,
                "REGISTRO_PARCELAS_NO_DISPONIBLE",
                "El catálogo de parcelas no está disponible",
                "No se pudo leer ni guardar la ficha porque el almacén de la red no respondió. No falló el nombre, el suelo ni las coordenadas; reintenta cuando la base de parcelas vuelva a estar en línea.",
                causa
        );
    }
}
