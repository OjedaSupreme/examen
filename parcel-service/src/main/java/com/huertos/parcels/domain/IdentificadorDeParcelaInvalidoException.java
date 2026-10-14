package com.huertos.parcels.domain;

import com.huertos.parcels.shared.Citas;
import org.springframework.http.HttpStatus;

public class IdentificadorDeParcelaInvalidoException extends ExcepcionDeHuerto {

    public IdentificadorDeParcelaInvalidoException(String recibido) {
        super(
                HttpStatus.BAD_REQUEST,
                "IDENTIFICADOR_PARCELA_INVALIDO",
                "Ese código no identifica una parcela",
                recibido == null || recibido.isBlank()
                        ? "La consulta llegó sin el código de la parcela. Al dar de alta una zona, el registro devuelve un UUID; ese es el valor que abre la ficha y su estado de sensores."
                        : "El valor " + Citas.entreComillas(recibido)
                        + " no es un código de parcela de esta red. Se espera el UUID completo que se entregó al crear la zona, sin texto adicional ni espacios de más."
        );
    }
}
