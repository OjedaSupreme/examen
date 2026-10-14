package com.huertos.monitoring.domain;

import com.huertos.monitoring.shared.Citas;
import org.springframework.http.HttpStatus;

public class ParcelaSinAltaException extends ExcepcionDeMonitoreo {

    public ParcelaSinAltaException(String parcelId) {
        super(
                HttpStatus.NOT_FOUND,
                "PARCELA_SIN_ALTA",
                "La medición no tiene parcela donde guardarse",
                "No se guardó la lectura porque no existe una parcela con el identificador " + Citas.entreComillas(parcelId)
                        + ". Primero da de alta la zona en el servicio de parcelas y después asocia el sensor; el monitoreo no deja mediciones huérfanas."
        );
    }
}
