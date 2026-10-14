package com.huertos.monitoring.web.validation;

import com.huertos.monitoring.shared.Citas;
import com.huertos.monitoring.shared.InputSanitizer;
import com.huertos.monitoring.shared.MensajesMedicion;
import com.huertos.monitoring.web.dto.CreateReadingRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.List;

public class MedicionValidator implements ConstraintValidator<MedicionValida, CreateReadingRequest> {

    private final InputSanitizer sanitizer = new InputSanitizer();

    @Override
    public boolean isValid(CreateReadingRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true;
        }
        List<Fallo> fallos = new ArrayList<>();
        revisarParcela(request.getParcelId(), fallos);
        revisarCanal(request.getSoilMoisture(), "soilMoisture", "humedad del suelo", 0, 100, MensajesMedicion.HUMEDAD_AUSENTE, MensajesMedicion::humedadFuera, fallos);
        revisarCanal(request.getTemperature(), "temperature", "temperatura", -20, 60, MensajesMedicion.TEMPERATURA_AUSENTE, MensajesMedicion::temperaturaFuera, fallos);
        revisarCanal(request.getPh(), "ph", "pH", 0, 14, MensajesMedicion.PH_AUSENTE, MensajesMedicion::phFuera, fallos);
        revisarCanal(request.getNitrogen(), "nitrogen", "nitrógeno", 0, 500, MensajesMedicion.NITROGENO_AUSENTE, MensajesMedicion::nitrogenoFuera, fallos);
        revisarCanal(request.getPhosphorus(), "phosphorus", "fósforo", 0, 500, MensajesMedicion.FOSFORO_AUSENTE, MensajesMedicion::fosforoFuera, fallos);
        revisarCanal(request.getPotassium(), "potassium", "potasio", 0, 500, MensajesMedicion.POTASIO_AUSENTE, MensajesMedicion::potasioFuera, fallos);
        revisarCanal(request.getAirQualityIndex(), "airQualityIndex", "calidad del aire", 0, 500, MensajesMedicion.AIRE_AUSENTE, MensajesMedicion::aireFuera, fallos);
        if (fallos.isEmpty()) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        for (Fallo fallo : fallos) {
            context.buildConstraintViolationWithTemplate(fallo.motivo())
                    .addPropertyNode(fallo.campo())
                    .addConstraintViolation();
        }
        return false;
    }

    private void revisarParcela(String raw, List<Fallo> fallos) {
        if (sanitizer.enBlanco(raw)) {
            fallos.add(new Fallo("parcelId", MensajesMedicion.PARCELA_AUSENTE));
            return;
        }
        if (sanitizer.contieneContenidoBloqueado(raw)) {
            fallos.add(new Fallo("parcelId", MensajesMedicion.PARCELA_BLOQUEADA));
            return;
        }
        String limpio = sanitizer.sanitize(raw);
        if (!sanitizer.esUuid(limpio)) {
            fallos.add(new Fallo("parcelId", MensajesMedicion.parcelaInvalida(Citas.entreComillas(limpio))));
        }
    }

    private void revisarCanal(Double valor, String campo, String canal, double minimo, double maximo,
                              String ausente, java.util.function.DoubleFunction<String> fuera, List<Fallo> fallos) {
        if (valor == null) {
            fallos.add(new Fallo(campo, ausente));
            return;
        }
        if (valor.isNaN() || valor.isInfinite()) {
            fallos.add(new Fallo(campo, MensajesMedicion.noFinito(canal)));
            return;
        }
        if (valor < minimo || valor > maximo) {
            fallos.add(new Fallo(campo, fuera.apply(valor)));
        }
    }

    private record Fallo(String campo, String motivo) {
    }
}
