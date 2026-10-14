package com.huertos.monitoring.web;

import com.huertos.monitoring.application.MonitoringApplicationService;
import com.huertos.monitoring.domain.ParcelaSinAltaException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConditionController.class)
class ConditionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MonitoringApplicationService service;

    @Test
    void temperaturaFueraDeEscalaCitaLosGradosRecibidos() throws Exception {
        mockMvc.perform(post("/api/v1/conditions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "parcelId":"11111111-1111-1111-1111-111111111111",
                                  "soilMoisture":35,
                                  "temperature":80,
                                  "ph":6.7,
                                  "nitrogen":20,
                                  "phosphorus":14,
                                  "potassium":18,
                                  "airQualityIndex":40
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("MEDICION_RECHAZADA"))
                .andExpect(jsonPath("$.titulo").value("La temperatura no se puede registrar"))
                .andExpect(jsonPath("$.explicacion").value(org.hamcrest.Matchers.containsString("80")))
                .andExpect(jsonPath("$.explicacion").value(org.hamcrest.Matchers.containsString("otra unidad")))
                .andExpect(jsonPath("$.fallos[0].campo").value("temperature"));
    }

    @Test
    void phEnTextoNombraElCanalQueNoEsNumero() throws Exception {
        mockMvc.perform(post("/api/v1/conditions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "parcelId":"11111111-1111-1111-1111-111111111111",
                                  "soilMoisture":35,
                                  "temperature":22,
                                  "ph":"alcalino",
                                  "nitrogen":20,
                                  "phosphorus":14,
                                  "potassium":18,
                                  "airQualityIndex":40
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("MEDICION_ILEGIBLE"))
                .andExpect(jsonPath("$.explicacion").value(org.hamcrest.Matchers.containsString("pH")))
                .andExpect(jsonPath("$.explicacion").value(org.hamcrest.Matchers.containsString("alcalino")));
    }

    @Test
    void parcelaSinAltaExplicaQueLaLecturaQuedoHuerfana() throws Exception {
        when(service.register(any())).thenThrow(new ParcelaSinAltaException("11111111-1111-1111-1111-111111111111"));

        mockMvc.perform(post("/api/v1/conditions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "parcelId":"11111111-1111-1111-1111-111111111111",
                                  "soilMoisture":35,
                                  "temperature":22,
                                  "ph":6.7,
                                  "nitrogen":20,
                                  "phosphorus":14,
                                  "potassium":18,
                                  "airQualityIndex":40
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("PARCELA_SIN_ALTA"))
                .andExpect(jsonPath("$.titulo").value("La medición no tiene parcela donde guardarse"))
                .andExpect(jsonPath("$.explicacion").value(org.hamcrest.Matchers.containsString("mediciones huérfanas")));
    }
}
