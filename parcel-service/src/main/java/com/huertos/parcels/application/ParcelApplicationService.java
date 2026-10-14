package com.huertos.parcels.application;

import com.huertos.parcels.domain.ContenidoNoAdmitidoException;
import com.huertos.parcels.domain.FichaInservibleException;
import com.huertos.parcels.domain.GeoLocation;
import com.huertos.parcels.domain.IdentificadorDeParcelaInvalidoException;
import com.huertos.parcels.domain.Parcel;
import com.huertos.parcels.domain.ParcelRepository;
import com.huertos.parcels.domain.ParcelaDuplicadaException;
import com.huertos.parcels.domain.ParcelaNoRegistradaException;
import com.huertos.parcels.domain.RegistroNoDisponibleException;
import com.huertos.parcels.domain.SoilType;
import com.huertos.parcels.shared.Citas;
import com.huertos.parcels.shared.InputSanitizer;
import com.huertos.parcels.web.dto.CreateParcelRequest;
import com.huertos.parcels.web.dto.CurrentStatusResponse;
import com.huertos.parcels.web.dto.LocationResponse;
import com.huertos.parcels.web.dto.ParcelResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class ParcelApplicationService {

    private final ParcelRepository repository;
    private final InputSanitizer sanitizer;
    private final MonitoringClient monitoringClient;

    public ParcelApplicationService(ParcelRepository repository,
                                    InputSanitizer sanitizer,
                                    MonitoringClient monitoringClient) {
        this.repository = repository;
        this.sanitizer = sanitizer;
        this.monitoringClient = monitoringClient;
    }

    public ParcelResponse create(CreateParcelRequest request) {
        String name = textoLimpio(request.getName(), "nombre de la parcela");
        String owner = textoLimpio(request.getOwnerOrLead(), "responsable");
        if (name.length() < 3) {
            throw new FichaInservibleException(
                    "El nombre no sobrevivió a la limpieza",
                    "Al quitar espacios de más y caracteres de control, el nombre de la parcela quedó inservible. Hace falta un nombre de al menos 3 caracteres que los voluntarios reconozcan en campo."
            );
        }
        SoilType soil = SoilType.parse(sanitizer.sanitize(request.getSoilType()))
                .orElseThrow(() -> new FichaInservibleException(
                        "Ese suelo no se puede guardar",
                        "Después de limpiar el texto, el suelo " + Citas.entreComillas(request.getSoilType())
                                + " sigue sin ser arcilloso, limoso o arenoso. La parcela no se abrió."
                ));
        if (request.getLocation() == null || request.getLocation().getLatitude() == null || request.getLocation().getLongitude() == null) {
            throw new FichaInservibleException(
                    "La parcela quedó sin un punto GPS utilizable",
                    "La ficha no trae latitud y longitud numéricas. Sin ese punto la zona no entra al mapa de la red."
            );
        }

        String normalized = name.toLowerCase(Locale.ROOT);
        try {
            if (repository.existsByNameNormalized(normalized)) {
                throw new ParcelaDuplicadaException(name);
            }
            Parcel parcel = new Parcel();
            parcel.setId(UUID.randomUUID().toString());
            parcel.setName(name);
            parcel.setNameNormalized(normalized);
            parcel.setLocation(new GeoLocation(request.getLocation().getLatitude(), request.getLocation().getLongitude()));
            parcel.setSoilType(soil);
            parcel.setOwnerOrLead(owner);
            parcel.setCreatedAt(Instant.now());
            repository.save(parcel);
            return toResponse(parcel, CurrentStatusResponse.sinLectura());
        } catch (ParcelaDuplicadaException ex) {
            throw ex;
        } catch (DuplicateKeyException ex) {
            throw new ParcelaDuplicadaException(name);
        } catch (DataAccessException ex) {
            throw new RegistroNoDisponibleException(ex);
        }
    }

    public List<ParcelResponse> list() {
        try {
            return repository.findAllByOrderByCreatedAtDesc().stream()
                    .map(parcel -> toResponse(parcel, null))
                    .toList();
        } catch (DataAccessException ex) {
            throw new RegistroNoDisponibleException(ex);
        }
    }

    public ParcelResponse getById(String rawId) {
        if (sanitizer.contieneContenidoBloqueado(rawId)) {
            throw new ContenidoNoAdmitidoException("identificador de la parcela");
        }
        String id = sanitizer.sanitize(rawId);
        if (!sanitizer.esUuid(id)) {
            throw new IdentificadorDeParcelaInvalidoException(rawId);
        }
        try {
            Parcel parcel = repository.findById(id)
                    .orElseThrow(() -> new ParcelaNoRegistradaException(id));
            return toResponse(parcel, monitoringClient.latest(id));
        } catch (ParcelaNoRegistradaException ex) {
            throw ex;
        } catch (DataAccessException ex) {
            throw new RegistroNoDisponibleException(ex);
        }
    }

    private String textoLimpio(String raw, String dato) {
        sanitizer.assertLimpio(dato, raw);
        String limpio = sanitizer.sanitize(raw);
        if (limpio == null || limpio.isBlank()) {
            throw new FichaInservibleException(
                    "Un dato de la ficha quedó vacío al limpiarlo",
                    "El " + dato + " llegó vacío o solo con espacios y caracteres de control. La parcela no se registró."
            );
        }
        return limpio;
    }

    private ParcelResponse toResponse(Parcel parcel, CurrentStatusResponse status) {
        GeoLocation location = parcel.getLocation();
        return new ParcelResponse(
                parcel.getId(),
                parcel.getName(),
                new LocationResponse(location.getLatitude(), location.getLongitude()),
                parcel.getSoilType().apiValue(),
                parcel.getOwnerOrLead(),
                parcel.getCreatedAt(),
                status
        );
    }
}
