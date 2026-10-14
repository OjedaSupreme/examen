package com.huertos.parcels.domain;

import org.springframework.http.HttpStatus;

public class ContenidoNoAdmitidoException extends ExcepcionDeHuerto {

    public ContenidoNoAdmitidoException(String dato) {
        super(
                HttpStatus.BAD_REQUEST,
                "CONTENIDO_NO_ADMITIDO",
                "Hay texto que no puede entrar a la ficha",
                "El " + dato + " incluye etiquetas, scripts o operadores de consulta. Ese texto se detuvo antes de tocar el catálogo de parcelas; vuelve a escribirlo en lenguaje plano."
        );
    }
}
