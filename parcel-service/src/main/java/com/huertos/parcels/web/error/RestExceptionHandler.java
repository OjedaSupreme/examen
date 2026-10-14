package com.huertos.parcels.web.error;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.huertos.parcels.domain.ExcepcionDeHuerto;
import com.huertos.parcels.domain.RegistroNoDisponibleException;
import com.huertos.parcels.shared.Citas;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
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

    @ExceptionHandler(ExcepcionDeHuerto.class)
    public ResponseEntity<RespuestaError> reglaDeHuerto(ExcepcionDeHuerto ex, HttpServletRequest request) {
        if (ex.getEstado().is5xxServerError()) {
            log.error("Fallo de parcelas [{}] en {}", ex.getCodigo(), request.getRequestURI(), ex);
        } else {
            log.warn("Rechazo de parcelas [{}] en {}: {}", ex.getCodigo(), request.getRequestURI(), ex.getExplicacion());
        }
        return respuesta(ex.getEstado(), ex.getCodigo(), ex.getTitulo(), ex.getExplicacion(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespuestaError> fichaRechazada(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> unicos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                unicos.putIfAbsent(error.getField(), error.getDefaultMessage())
        );
        if (unicos.isEmpty()) {
            ex.getBindingResult().getGlobalErrors().forEach(error ->
                    unicos.putIfAbsent("ficha", error.getDefaultMessage())
            );
        }
        List<FalloCampo> fallos = unicos.entrySet().stream()
                .map(entry -> new FalloCampo(entry.getKey(), entry.getValue()))
                .toList();
        String titulo;
        String explicacion;
        if (fallos.size() == 1) {
            titulo = tituloPorCampo(fallos.get(0).campo());
            explicacion = fallos.get(0).motivo();
        } else {
            titulo = "La ficha de la parcela no se pudo dar de alta";
            explicacion = "No se abrió la zona en el catálogo. Hay " + fallos.size()
                    + " datos que no cumplen las reglas de la red y ninguno se guardó.";
        }
        log.warn("Ficha de parcela rechazada en {}: {}", request.getRequestURI(), unicos.keySet());
        return respuesta(HttpStatus.BAD_REQUEST, "FICHA_PARCELA_RECHAZADA", titulo, explicacion, request, fallos);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<RespuestaError> restricciones(ConstraintViolationException ex, HttpServletRequest request) {
        List<FalloCampo> fallos = ex.getConstraintViolations().stream()
                .map(violation -> new FalloCampo(violation.getPropertyPath().toString(), violation.getMessage()))
                .toList();
        String explicacion = fallos.isEmpty()
                ? "La consulta de la parcela trae un dato que la red no puede usar."
                : fallos.get(0).motivo();
        return respuesta(HttpStatus.BAD_REQUEST, "FICHA_PARCELA_RECHAZADA", "La consulta de la parcela no se pudo armar", explicacion, request, fallos);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RespuestaError> cuerpoIlegible(HttpMessageNotReadableException ex, HttpServletRequest request) {
        String explicacion = explicarCuerpo(ex);
        log.warn("Cuerpo de parcela ilegible en {}: {}", request.getRequestURI(), ex.getMessage());
        return respuesta(HttpStatus.BAD_REQUEST, "FICHA_ILEGIBLE", "La ficha de la parcela no se pudo leer", explicacion, request, null);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<RespuestaError> formato(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        String recibido = ex.getContentType() == null ? "sin Content-Type" : ex.getContentType().toString();
        return respuesta(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "FORMATO_FICHA_NO_ADMITIDO",
                "El formato de la ficha no se puede leer",
                "La parcela tiene que llegar como JSON (Content-Type: application/json). El tipo " + Citas.entreComillas(recibido) + " no lo interpreta el registro.",
                request,
                null
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<RespuestaError> metodo(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return respuesta(
                HttpStatus.METHOD_NOT_ALLOWED,
                "OPERACION_NO_APLICA_A_PARCELAS",
                "Esa operación no aplica a las parcelas",
                "Llegó un " + ex.getMethod() + " y este recurso no lo usa. Puedes dar de alta una zona con POST, listar las que ya están en la red con GET o abrir el detalle de una con GET y su identificador.",
                request,
                null
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<RespuestaError> rutaAjena(NoResourceFoundException ex, HttpServletRequest request) {
        return respuesta(
                HttpStatus.NOT_FOUND,
                "RUTA_AJENA_A_PARCELAS",
                "Esa ruta no consulta parcelas",
                "La ruta " + Citas.entreComillas(request.getRequestURI()) + " no pertenece al servicio de parcelas. Para trabajar con zonas de cultivo usa /api/v1/parcels: alta, listado o detalle por identificador.",
                request,
                null
        );
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<RespuestaError> almacen(DataAccessException ex, HttpServletRequest request) {
        log.error("El almacén de parcelas no respondió en {}", request.getRequestURI(), ex);
        RegistroNoDisponibleException traducida = new RegistroNoDisponibleException(ex);
        return respuesta(traducida.getEstado(), traducida.getCodigo(), traducida.getTitulo(), traducida.getExplicacion(), request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespuestaError> imprevisto(Exception ex, HttpServletRequest request) {
        log.error("Fallo imprevisto en parcelas {}", request.getRequestURI(), ex);
        return respuesta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "FALLO_INTERNO_PARCELAS",
                "La operación sobre parcelas se detuvo",
                "Ocurrió un fallo imprevisto dentro del servicio de parcelas y se abortó la operación para no dejar una ficha incompleta en el catálogo. El detalle técnico quedó en el registro interno, no en esta respuesta.",
                request,
                null
        );
    }

    private String explicarCuerpo(HttpMessageNotReadableException ex) {
        Throwable cursor = ex;
        while (cursor != null) {
            if (cursor instanceof InvalidFormatException invalid) {
                String campo = ruta(invalid);
                String valor = String.valueOf(invalid.getValue());
                if (campo.contains("latitude") || campo.contains("longitude")) {
                    return "El dato " + campo + " llegó como " + Citas.entreComillas(valor)
                            + " y la coordenada tiene que ser un número. Sin latitud y longitud numéricas la parcela no queda en el mapa.";
                }
                return "El dato " + (campo.isBlank() ? "de la ficha" : campo) + " llegó como " + Citas.entreComillas(valor)
                        + " y no tiene el tipo que guarda el registro de parcelas.";
            }
            if (cursor instanceof UnrecognizedPropertyException desconocida) {
                return "La ficha trae el dato " + Citas.entreComillas(desconocida.getPropertyName())
                        + ", y el registro de parcelas no lo conoce. Los datos que sí se guardan son name, location.latitude, location.longitude, soilType y ownerOrLead.";
            }
            if (cursor instanceof MismatchedInputException mismatched && ruta(mismatched).contains("location")) {
                return "La ubicación llegó con una forma que no es un punto GPS. Se espera location con latitude y longitude numéricas; sin eso la parcela no se puede situar.";
            }
            if (cursor instanceof JsonParseException parse && parse.getLocation() != null && parse.getLocation().getLineNr() > 0) {
                return "El JSON de la parcela se rompe en la línea " + parse.getLocation().getLineNr()
                        + ", columna " + parse.getLocation().getColumnNr()
                        + ". El registro no llegó a revisar el nombre, el suelo ni el punto GPS.";
            }
            cursor = cursor.getCause();
        }
        String texto = ex.getMessage() == null ? "" : ex.getMessage();
        if (texto.contains("Required request body is missing")) {
            return "La solicitud llegó vacía. Para dar de alta una parcela hay que enviar el nombre de la zona, el punto GPS, el tipo de suelo (arcilloso, limoso o arenoso) y el responsable.";
        }
        return "El cuerpo no se pudo leer como ficha de parcela. Tiene que ser un JSON con name, location (latitude y longitude numéricas), soilType y ownerOrLead.";
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
        if ("name".equals(campo)) {
            return "El nombre de la parcela no se puede registrar";
        }
        if (campo.endsWith("latitude")) {
            return "La latitud no ubica la parcela";
        }
        if (campo.endsWith("longitude")) {
            return "La longitud no ubica la parcela";
        }
        if ("location".equals(campo) || campo.endsWith(".location")) {
            return "La parcela quedó sin un punto GPS utilizable";
        }
        if ("soilType".equals(campo)) {
            return "Ese tipo de suelo no entra en la red";
        }
        if ("ownerOrLead".equals(campo)) {
            return "El responsable de la parcela no quedó asentado";
        }
        return "Hay un dato de la parcela que la red no puede aceptar";
    }

    private ResponseEntity<RespuestaError> respuesta(HttpStatus estado, String codigo, String titulo, String explicacion,
                                                     HttpServletRequest request, List<FalloCampo> fallos) {
        RespuestaError body = new RespuestaError(
                Instant.now(),
                estado.value(),
                codigo,
                titulo,
                explicacion,
                request.getRequestURI(),
                fallos == null || fallos.isEmpty() ? null : fallos
        );
        return ResponseEntity.status(estado).body(body);
    }
}
