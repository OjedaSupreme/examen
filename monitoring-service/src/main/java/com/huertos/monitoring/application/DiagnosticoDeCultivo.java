package com.huertos.monitoring.application;

import com.huertos.monitoring.domain.SensorReading;

import java.util.ArrayList;
import java.util.List;

public final class DiagnosticoDeCultivo {

    private DiagnosticoDeCultivo() {
    }

    public static String resumir(SensorReading lectura) {
        List<String> notas = new ArrayList<>();
        if (lectura.getPh() < 5.5 || lectura.getPh() > 7.5) {
            notas.add("el pH quedó en " + fmt(lectura.getPh())
                    + ", fuera del rango favorable de 5.5 a 7.5, así que conviene valorar una enmienda antes de la siguiente siembra");
        }
        if (lectura.getSoilMoisture() < 25) {
            notas.add("la humedad del suelo quedó en " + fmt(lectura.getSoilMoisture())
                    + " %, por debajo de lo que sostiene el cultivo sin un riego o una capa de acolchado");
        } else if (lectura.getSoilMoisture() > 80) {
            notas.add("la humedad del suelo quedó en " + fmt(lectura.getSoilMoisture())
                    + " %, un exceso que puede asfixiar la raíz si no se revisa el drenaje");
        }
        if (lectura.getTemperature() < 10 || lectura.getTemperature() > 35) {
            notas.add("la temperatura quedó en " + fmt(lectura.getTemperature())
                    + " °C, fuera del rango habitual de cultivo (10 a 35 °C)");
        }
        if (lectura.getNitrogen() < 15) {
            notas.add("el nitrógeno quedó en " + fmt(lectura.getNitrogen())
                    + ", corto para sostener la regeneración de esta parcela; el compostaje ayudaría a reponerlo");
        }
        if (lectura.getPhosphorus() < 10) {
            notas.add("el fósforo quedó en " + fmt(lectura.getPhosphorus())
                    + ", por debajo de lo que suele pedir un suelo en recuperación");
        }
        if (lectura.getPotassium() < 15) {
            notas.add("el potasio quedó en " + fmt(lectura.getPotassium())
                    + ", insuficiente para el cierre de cultivo que espera esta red");
        }
        if (lectura.getAirQualityIndex() > 100) {
            notas.add("la calidad del aire marcó " + fmt(lectura.getAirQualityIndex())
                    + ", un nivel en el que conviene acortar la faena de los voluntarios en esta zona");
        }
        if (notas.isEmpty()) {
            return "La medición vigente está en rango favorable para esta parcela: humedad "
                    + fmt(lectura.getSoilMoisture()) + " %, temperatura " + fmt(lectura.getTemperature())
                    + " °C, pH " + fmt(lectura.getPh()) + ", nitrógeno " + fmt(lectura.getNitrogen())
                    + ", fósforo " + fmt(lectura.getPhosphorus()) + ", potasio " + fmt(lectura.getPotassium())
                    + " y calidad del aire " + fmt(lectura.getAirQualityIndex()) + ".";
        }
        return "La medición más reciente de esta parcela pide atención: " + String.join("; ", notas) + ".";
    }

    private static String fmt(double valor) {
        if (valor == Math.rint(valor)) {
            return String.valueOf((long) valor);
        }
        return String.format(java.util.Locale.US, "%.1f", valor);
    }
}
