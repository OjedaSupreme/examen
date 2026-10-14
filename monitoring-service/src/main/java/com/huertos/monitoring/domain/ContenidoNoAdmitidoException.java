package com.huertos.monitoring.domain;

import org.springframework.http.HttpStatus;

public class ContenidoNoAdmitidoException extends ExcepcionDeMonitoreo {

    public ContenidoNoAdmitidoException(String dato) {
        super(
                HttpStatus.BAD_REQUEST,
                "CONTENIDO_NO_ADMITIDO",
                "Hay texto que no puede entrar a la medición",
                "El " + dato + " incluye etiquetas, scripts o operadores de consulta. Esa lectura se detuvo antes de tocar el historial de sensores; vuelve a enviarla en lenguaje plano."
        );
    }
}
