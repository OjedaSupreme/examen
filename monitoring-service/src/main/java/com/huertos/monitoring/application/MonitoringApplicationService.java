package com.huertos.monitoring.application;

import com.huertos.monitoring.domain.ContenidoNoAdmitidoException;
import com.huertos.monitoring.domain.HistorialNoDisponibleException;
import com.huertos.monitoring.domain.IdentificadorDeMedicionInvalidoException;
import com.huertos.monitoring.domain.SensorReading;
import com.huertos.monitoring.domain.SensorReadingRepository;
import com.huertos.monitoring.shared.InputSanitizer;
import com.huertos.monitoring.web.dto.CreateReadingRequest;
import com.huertos.monitoring.web.dto.CurrentStatusResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class MonitoringApplicationService {

    private final SensorReadingRepository repository;
    private final InputSanitizer sanitizer;
    private final ParcelCatalog parcelCatalog;

    public MonitoringApplicationService(SensorReadingRepository repository,
                                        InputSanitizer sanitizer,
                                        ParcelCatalog parcelCatalog) {
        this.repository = repository;
        this.sanitizer = sanitizer;
        this.parcelCatalog = parcelCatalog;
    }

    public CurrentStatusResponse register(CreateReadingRequest request) {
        String parcelId = identificador(request.getParcelId());
        parcelCatalog.assertExists(parcelId);
        SensorReading reading = new SensorReading();
        reading.setId(UUID.randomUUID().toString());
        reading.setParcelId(parcelId);
        reading.setSoilMoisture(request.getSoilMoisture());
        reading.setTemperature(request.getTemperature());
        reading.setPh(request.getPh());
        reading.setNitrogen(request.getNitrogen());
        reading.setPhosphorus(request.getPhosphorus());
        reading.setPotassium(request.getPotassium());
        reading.setAirQualityIndex(request.getAirQualityIndex());
        reading.setRecordedAt(Instant.now());
        try {
            repository.save(reading);
        } catch (DataAccessException ex) {
            throw new HistorialNoDisponibleException(ex);
        }
        return vigente(reading);
    }

    public CurrentStatusResponse latest(String rawParcelId) {
        if (sanitizer.contieneContenidoBloqueado(rawParcelId)) {
            throw new ContenidoNoAdmitidoException("identificador de la parcela");
        }
        String parcelId = sanitizer.sanitize(rawParcelId);
        if (!sanitizer.esUuid(parcelId)) {
            throw new IdentificadorDeMedicionInvalidoException(rawParcelId);
        }
        try {
            return repository.findFirstByParcelIdOrderByRecordedAtDesc(parcelId)
                    .map(this::vigente)
                    .orElseGet(() -> CurrentStatusResponse.sinLectura(parcelId));
        } catch (DataAccessException ex) {
            throw new HistorialNoDisponibleException(ex);
        }
    }

    private String identificador(String raw) {
        sanitizer.assertLimpio("código de parcela", raw);
        String limpio = sanitizer.sanitize(raw);
        if (!sanitizer.esUuid(limpio)) {
            throw new IdentificadorDeMedicionInvalidoException(raw);
        }
        return limpio;
    }

    private CurrentStatusResponse vigente(SensorReading reading) {
        return new CurrentStatusResponse(
                true,
                "MEDICION_VIGENTE",
                DiagnosticoDeCultivo.resumir(reading),
                reading.getSoilMoisture(),
                reading.getTemperature(),
                reading.getPh(),
                reading.getNitrogen(),
                reading.getPhosphorus(),
                reading.getPotassium(),
                reading.getAirQualityIndex(),
                reading.getRecordedAt()
        );
    }
}
