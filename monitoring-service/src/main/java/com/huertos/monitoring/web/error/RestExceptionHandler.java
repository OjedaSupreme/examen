package com.huertos.monitoring.web.error;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.huertos.monitoring.domain.ExcepcionDeMonitoreo;
import com.huertos.monitoring.domain.HistorialNoDisponibleException;
import com.huertos.monitoring.shared.Citas;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(ExcepcionDeMonitoreo.class)
    public ResponseEntity<RespuestaError> regla(ExcepcionDeMonitoreo ex, HttpServletRequest request) {
        if (ex.getEstado().is5xxServerError()) {
            log.error("Fallo de monitoreo [{}] en {}", ex.getCodigo(), request.getRequestURI(), ex);
        } else {
            log.warn("Rechazo de monitoreo [{}] en {}: {}", ex.getCodigo(), request.getRequestURI(), ex.getExplicacion());
        }
        return respuesta(ex.getEstado(), ex.getCodigo(), ex.getTitulo(), ex.getExplicacion(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespuestaError> medicionRechazada(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> unicos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                unicos.putIfAbsent(error.getField(), error.getDefaultMessage())
        );
        List<FalloCampo> fallos = unicos.entrySet().stream()
                .map(entry -> new FalloCampo(entry.getKey(), entry.getValue()))
                .toList();
        String titulo;
        String explicacion;
        if (fallos.size() == 1) {
            titulo = tituloPorCampo(fallos.get(0).campo());
            explicacion = fallos.get(0).motivo();
        } else {
            titulo = "La medición del sensor no se pudo guardar";
            explicacion = "No se escribió la lectura en el historial. Hay " + fallos.size()
                    + " canales que no cumplen la escala de la red y ninguno se guardó.";
        }
        log.warn("Medición rechazada en {}: {}", request.getRequestURI(), unicos.keySet());
        return respuesta(HttpStatus.BAD_REQUEST, "MEDICION_RECHAZADA", titulo, explicacion, request, fallos);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RespuestaError> cuerpoIlegible(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("Cuerpo de medición ilegible en {}: {}", request.getRequestURI(), ex.getMessage());
        return respuesta(HttpStatus.BAD_REQUEST, "MEDICION_ILEGIBLE", "La medición no se pudo leer", explicarCuerpo(ex), request, null);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<RespuestaError> formato(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        String recibido = ex.getContentType() == null ? "sin Content-Type" : ex.getContentType().toString();
        return respuesta(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "FORMATO_MEDICION_NO_ADMITIDO",
                "El formato de la medición no se puede leer",
                "La lectura tiene que llegar como JSON (Content-Type: application/json). El tipo " + Citas.entreComillas(recibido) + " no lo interpreta el monitoreo.",
                request,
                null
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<RespuestaError> metodo(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return respuesta(
                HttpStatus.METHOD_NOT_ALLOWED,
                "OPERACION_NO_APLICA_AL_MONITOREO",
                "Esa operación no aplica a las mediciones",
                "Llegó un " + ex.getMethod() + " y este recurso no lo usa. Puedes guardar una lectura con POST /api/v1/conditions o consultar el estado vigente con GET /api/v1/conditions/latest/{parcelId}.",
                request,
                null
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<RespuestaError> rutaAjena(NoResourceFoundException ex, HttpServletRequest request) {
        return respuesta(
                HttpStatus.NOT_FOUND,
                "RUTA_AJENA_AL_MONITOREO",
                "Esa ruta no consulta sensores",
                "La ruta " + Citas.entreComillas(request.getRequestURI())
                        + " no pertenece al monitoreo. El estado de suelo y aire vive en /api/v1/conditions.",
                request,
                null
        );
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<RespuestaError> almacen(DataAccessException ex, HttpServletRequest request) {
        log.error("El historial de sensores no respondió en {}", request.getRequestURI(), ex);
        HistorialNoDisponibleException traducida = new HistorialNoDisponibleException(ex);
        return respuesta(traducida.getEstado(), traducida.getCodigo(), traducida.getTitulo(), traducida.getExplicacion(), request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespuestaError> imprevisto(Exception ex, HttpServletRequest request) {
        log.error("Fallo imprevisto en monitoreo {}", request.getRequestURI(), ex);
        return respuesta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "FALLO_INTERNO_MONITOREO",
                "La lectura se detuvo antes de publicarse",
                "Ocurrió un fallo imprevisto dentro del monitoreo y se abortó la operación para no publicar un estado a medias de la parcela. El detalle técnico quedó en el registro interno, no en esta respuesta.",
                request,
                null
        );
    }

    private String explicarCuerpo(HttpMessageNotReadableException ex) {
        Throwable cursor = ex;
        while (cursor != null) {
            if (cursor instanceof InvalidFormatException invalid) {
                String campo = ruta(invalid);
                String canal = etiqueta(campo);
                return "El canal " + canal + " llegó como " + Citas.entreComillas(String.valueOf(invalid.getValue()))
                        + " y ese sensor reporta un número. La medición no se guardó.";
            }
            if (cursor instanceof UnrecognizedPropertyException desconocida) {
                return "La medición trae el dato " + Citas.entreComillas(desconocida.getPropertyName())
                        + ", y el monitoreo no lo reconoce. Los canales que sí se guardan son parcelId, soilMoisture, temperature, ph, nitrogen, phosphorus, potassium y airQualityIndex.";
            }
            if (cursor instanceof JsonParseException parse && parse.getLocation() != null && parse.getLocation().getLineNr() > 0) {
                return "El JSON de la medición se rompe en la línea " + parse.getLocation().getLineNr()
                        + ", columna " + parse.getLocation().getColumnNr()
                        + ". El monitoreo no llegó a revisar humedad, pH ni calidad del aire.";
            }
            cursor = cursor.getCause();
        }
        String texto = ex.getMessage() == null ? "" : ex.getMessage();
        if (texto.contains("Required request body is missing")) {
            return "La solicitud llegó vacía. Para guardar el estado del sensor hay que enviar la parcela y las lecturas de humedad, temperatura, pH, nitrógeno, fósforo, potasio y calidad del aire.";
        }
        return "El cuerpo no se pudo leer como medición. Tiene que ser un JSON con la parcela y valores numéricos de humedad, temperatura, pH, nitrógeno, fósforo, potasio y calidad del aire.";
    }

    private String etiqueta(String campo) {
        return switch (campo) {
            case "soilMoisture" -> "humedad del suelo";
            case "temperature" -> "temperatura";
            case "ph" -> "pH";
            case "nitrogen" -> "nitrógeno";
            case "phosphorus" -> "fósforo";
            case "potassium" -> "potasio";
            case "airQualityIndex" -> "calidad del aire";
            case "parcelId" -> "código de parcela";
            default -> campo == null || campo.isBlank() ? "de la medición" : campo;
        };
    }

    private String ruta(JsonMappingException ex) {
        List<String> partes = new ArrayList<>();
        for (JsonMappingException.Reference ref : ex.getPath()) {
            if (ref.getFieldName() != null) {
                partes.add(ref.getFieldName());
            }
        }
        return String.join(".", partes);
    }

    private String tituloPorCampo(String campo) {
        return switch (campo) {
            case "parcelId" -> "La medición no quedó ligada a una parcela";
            case "soilMoisture" -> "La humedad del suelo no se puede registrar";
            case "temperature" -> "La temperatura no se puede registrar";
            case "ph" -> "El pH no se puede registrar";
            case "nitrogen" -> "El nitrógeno no se puede registrar";
            case "phosphorus" -> "El fósforo no se puede registrar";
            case "potassium" -> "El potasio no se puede registrar";
            case "airQualityIndex" -> "La calidad del aire no se puede registrar";
            default -> "Hay un canal del sensor que la red no puede aceptar";
        };
    }

    private ResponseEntity<RespuestaError> respuesta(HttpStatus estado, String codigo, String titulo, String explicacion,
                                                     HttpServletRequest request, List<FalloCampo> fallos) {
        return ResponseEntity.status(estado).body(new RespuestaError(
                Instant.now(),
                estado.value(),
                codigo,
                titulo,
                explicacion,
                request.getRequestURI(),
                fallos == null || fallos.isEmpty() ? null : fallos
        ));
    }
}
