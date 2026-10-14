package com.huertos.parcels.web.validation;

import com.huertos.parcels.domain.SoilType;
import com.huertos.parcels.shared.Citas;
import com.huertos.parcels.shared.InputSanitizer;
import com.huertos.parcels.shared.MensajesParcela;
import com.huertos.parcels.web.dto.CreateParcelRequest;
import com.huertos.parcels.web.dto.LocationRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FichaParcelaValidator implements ConstraintValidator<FichaParcelaValida, CreateParcelRequest> {

    private static final Pattern NOMBRE = Pattern.compile("^[\\p{L}0-9][\\p{L}0-9\\s.'#,\\-]{0,119}$");
    private static final Pattern RESPONSABLE = Pattern.compile("^[\\p{L}][\\p{L}\\s.'’\\-]{0,79}$");

    private final InputSanitizer sanitizer = new InputSanitizer();

    @Override
    public boolean isValid(CreateParcelRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true;
        }
        List<Fallo> fallos = new ArrayList<>();
        revisarNombre(request.getName(), fallos);
        revisarUbicacion(request.getLocation(), fallos);
        revisarSuelo(request.getSoilType(), fallos);
        revisarResponsable(request.getOwnerOrLead(), fallos);
        if (fallos.isEmpty()) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        for (Fallo fallo : fallos) {
            var nodo = context.buildConstraintViolationWithTemplate(fallo.motivo()).addPropertyNode(fallo.campo());
            if (fallo.anidado() != null) {
                nodo.addPropertyNode(fallo.anidado()).addConstraintViolation();
            } else {
                nodo.addConstraintViolation();
            }
        }
        return false;
    }

    private void revisarNombre(String raw, List<Fallo> fallos) {
        if (sanitizer.enBlanco(raw)) {
            fallos.add(Fallo.de("name", null, MensajesParcela.NOMBRE_AUSENTE));
            return;
        }
        if (sanitizer.contieneContenidoBloqueado(raw)) {
            fallos.add(Fallo.de("name", null, MensajesParcela.NOMBRE_BLOQUEADO));
            return;
        }
        String limpio = sanitizer.sanitize(raw);
        if (limpio.length() < 3) {
            fallos.add(Fallo.de("name", null, MensajesParcela.nombreCorto(limpio.length())));
            return;
        }
        if (limpio.length() > 120) {
            fallos.add(Fallo.de("name", null, MensajesParcela.nombreLargo(limpio.length())));
            return;
        }
        if (!NOMBRE.matcher(limpio).matches()) {
            fallos.add(Fallo.de("name", null, MensajesParcela.NOMBRE_CARACTERES));
        }
    }

    private void revisarUbicacion(LocationRequest location, List<Fallo> fallos) {
        if (location == null) {
            fallos.add(Fallo.de("location", null, MensajesParcela.UBICACION_AUSENTE));
            return;
        }
        revisarCoordenada(location.getLatitude(), true, fallos);
        revisarCoordenada(location.getLongitude(), false, fallos);
    }

    private void revisarCoordenada(Double valor, boolean latitud, List<Fallo> fallos) {
        String anidado = latitud ? "latitude" : "longitude";
        if (valor == null) {
            fallos.add(Fallo.de("location", anidado, latitud ? MensajesParcela.LATITUD_AUSENTE : MensajesParcela.LONGITUD_AUSENTE));
            return;
        }
        if (valor.isNaN() || valor.isInfinite()) {
            fallos.add(Fallo.de("location", anidado, latitud ? MensajesParcela.LATITUD_NO_FINITA : MensajesParcela.LONGITUD_NO_FINITA));
            return;
        }
        if (latitud && (valor < -90 || valor > 90)) {
            fallos.add(Fallo.de("location", anidado, MensajesParcela.latitudFuera(valor)));
        }
        if (!latitud && (valor < -180 || valor > 180)) {
            fallos.add(Fallo.de("location", anidado, MensajesParcela.longitudFuera(valor)));
        }
    }

    private void revisarSuelo(String raw, List<Fallo> fallos) {
        if (sanitizer.enBlanco(raw)) {
            fallos.add(Fallo.de("soilType", null, MensajesParcela.SUELO_AUSENTE));
            return;
        }
        if (sanitizer.contieneContenidoBloqueado(raw)) {
            fallos.add(Fallo.de("soilType", null, MensajesParcela.SUELO_BLOQUEADO));
            return;
        }
        String limpio = sanitizer.sanitize(raw);
        if (SoilType.parse(limpio).isEmpty()) {
            fallos.add(Fallo.de("soilType", null, MensajesParcela.sueloDesconocido(Citas.entreComillas(limpio))));
        }
    }

    private void revisarResponsable(String raw, List<Fallo> fallos) {
        if (sanitizer.enBlanco(raw)) {
            fallos.add(Fallo.de("ownerOrLead", null, MensajesParcela.RESPONSABLE_AUSENTE));
            return;
        }
        if (sanitizer.contieneContenidoBloqueado(raw)) {
            fallos.add(Fallo.de("ownerOrLead", null, MensajesParcela.RESPONSABLE_BLOQUEADO));
            return;
        }
        String limpio = sanitizer.sanitize(raw);
        if (limpio.length() < 3) {
            fallos.add(Fallo.de("ownerOrLead", null, MensajesParcela.responsableCorto(limpio.length())));
            return;
        }
        if (limpio.length() > 80) {
            fallos.add(Fallo.de("ownerOrLead", null, MensajesParcela.responsableLargo(limpio.length())));
            return;
        }
        if (!RESPONSABLE.matcher(limpio).matches()) {
            fallos.add(Fallo.de("ownerOrLead", null, MensajesParcela.RESPONSABLE_CARACTERES));
        }
    }

    private record Fallo(String campo, String anidado, String motivo) {
        private static Fallo de(String campo, String anidado, String motivo) {
            return new Fallo(campo, anidado, motivo);
        }
    }
}
