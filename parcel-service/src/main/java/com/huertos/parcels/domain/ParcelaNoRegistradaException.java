package com.huertos.parcels.domain;

import com.huertos.parcels.shared.Citas;
import org.springframework.http.HttpStatus;

public class ParcelaNoRegistradaException extends ExcepcionDeHuerto {

    public ParcelaNoRegistradaException(String id) {
        super(
                HttpStatus.NOT_FOUND,
                "PARCELA_NO_REGISTRADA",
                "Esa parcela no está en la red",
                "No hay ninguna zona dada de alta con el identificador " + Citas.entreComillas(id)
                        + ". Confirma que copiaste el código completo que devolvió el registro al crear la parcela; si la zona es nueva, primero hay que darla de alta."
        );
    }
}
