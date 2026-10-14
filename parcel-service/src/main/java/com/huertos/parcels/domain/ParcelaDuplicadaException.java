package com.huertos.parcels.domain;

import com.huertos.parcels.shared.Citas;
import org.springframework.http.HttpStatus;

public class ParcelaDuplicadaException extends ExcepcionDeHuerto {

    public ParcelaDuplicadaException(String nombre) {
        super(
                HttpStatus.CONFLICT,
                "PARCELA_YA_REGISTRADA",
                "Ese nombre de parcela ya está en la red",
                "Ya existe una zona dada de alta como " + Citas.entreComillas(nombre)
                        + ". El catálogo no admite dos parcelas con el mismo nombre visible, porque los voluntarios y las alertas de sensores se cruzarían. Usa un nombre que distinga esta parcela en campo."
        );
    }
}
