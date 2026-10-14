package com.huertos.monitoring.domain;

import com.huertos.monitoring.shared.Citas;
import org.springframework.http.HttpStatus;

public class IdentificadorDeMedicionInvalidoException extends ExcepcionDeMonitoreo {

    public IdentificadorDeMedicionInvalidoException(String recibido) {
        super(
                HttpStatus.BAD_REQUEST,
                "IDENTIFICADOR_MEDICION_INVALIDO",
                "La medición no quedó ligada a una parcela",
                recibido == null || recibido.isBlank()
                        ? "La consulta del estado llegó sin el código de la parcela. Hace falta el UUID que se entregó al dar de alta la zona."
                        : "El valor " + Citas.entreComillas(recibido)
                        + " no es el código de una parcela de esta red. El estado de sensores se consulta con el UUID completo de la zona, sin texto extra."
        );
    }
}
