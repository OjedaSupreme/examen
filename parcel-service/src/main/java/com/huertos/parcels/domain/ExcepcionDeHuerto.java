package com.huertos.parcels.domain;

import org.springframework.http.HttpStatus;

public class ExcepcionDeHuerto extends RuntimeException {

    private final HttpStatus estado;
    private final String codigo;
    private final String titulo;

    public ExcepcionDeHuerto(HttpStatus estado, String codigo, String titulo, String explicacion) {
        this(estado, codigo, titulo, explicacion, null);
    }

    public ExcepcionDeHuerto(HttpStatus estado, String codigo, String titulo, String explicacion, Throwable causa) {
        super(explicacion, causa);
        this.estado = estado;
        this.codigo = codigo;
        this.titulo = titulo;
    }

    public HttpStatus getEstado() {
        return estado;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getExplicacion() {
        return getMessage();
    }
}
