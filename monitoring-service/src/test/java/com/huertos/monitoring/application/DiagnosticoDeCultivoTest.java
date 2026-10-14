package com.huertos.monitoring.application;

import com.huertos.monitoring.domain.SensorReading;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DiagnosticoDeCultivoTest {

    @Test
    void phFueraDeRangoCitaLaCifraYLaEnmienda() {
        SensorReading lectura = base();
        lectura.setPh(8.4);

        String resumen = DiagnosticoDeCultivo.resumir(lectura);

        assertThat(resumen).contains("8.4").contains("enmienda").contains("pide atención");
    }

    @Test
    void lecturaFavorableDescribeLosCanalesQueSiEntraron() {
        String resumen = DiagnosticoDeCultivo.resumir(base());

        assertThat(resumen).contains("rango favorable").contains("humedad 40").contains("pH 6.5");
    }

    private SensorReading base() {
        SensorReading lectura = new SensorReading();
        lectura.setSoilMoisture(40);
        lectura.setTemperature(22);
        lectura.setPh(6.5);
        lectura.setNitrogen(30);
        lectura.setPhosphorus(18);
        lectura.setPotassium(22);
        lectura.setAirQualityIndex(42);
        return lectura;
    }
}
