package com.huertos.parcels.web;

import com.huertos.parcels.application.ParcelApplicationService;
import com.huertos.parcels.domain.ParcelaNoRegistradaException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ParcelController.class)
class ParcelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ParcelApplicationService service;

    @Test
    void nombreVacioExplicaLaZonaQueNoSePudoAbrir() throws Exception {
        mockMvc.perform(post("/api/v1/parcels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"  ","location":{"latitude":19.4,"longitude":-99.1},"soilType":"arcilloso","ownerOrLead":"Ana López"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("FICHA_PARCELA_RECHAZADA"))
                .andExpect(jsonPath("$.titulo").value("El nombre de la parcela no se puede registrar"))
                .andExpect(jsonPath("$.explicacion").value(org.hamcrest.Matchers.containsString("llegó sin nombre")))
                .andExpect(jsonPath("$.fallos[0].campo").value("name"));
        verify(service, never()).create(any());
    }

    @Test
    void latitudImposibleCitaElGradoRecibido() throws Exception {
        mockMvc.perform(post("/api/v1/parcels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Huerto Roma","location":{"latitude":190,"longitude":-99.1},"soilType":"limoso","ownerOrLead":"Ana López"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.titulo").value("La latitud no ubica la parcela"))
                .andExpect(jsonPath("$.explicacion").value(org.hamcrest.Matchers.containsString("190")))
                .andExpect(jsonPath("$.fallos[0].campo").value("location.latitude"));
    }

    @Test
    void sueloDesconocidoNombraLaTexturaRechazada() throws Exception {
        mockMvc.perform(post("/api/v1/parcels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Huerto Roma","location":{"latitude":19.4,"longitude":-99.1},"soilType":"pedregoso","ownerOrLead":"Ana López"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.titulo").value("Ese tipo de suelo no entra en la red"))
                .andExpect(jsonPath("$.explicacion").value(org.hamcrest.Matchers.containsString("pedregoso")))
                .andExpect(jsonPath("$.explicacion").value(org.hamcrest.Matchers.containsString("arcillosas")));
    }

    @Test
    void datoDesconocidoDiceCualesSiGuardaElRegistro() throws Exception {
        mockMvc.perform(post("/api/v1/parcels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Huerto Roma","alias":"norte","location":{"latitude":19.4,"longitude":-99.1},"soilType":"arenoso","ownerOrLead":"Ana López"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("FICHA_ILEGIBLE"))
                .andExpect(jsonPath("$.explicacion").value(org.hamcrest.Matchers.containsString("alias")))
                .andExpect(jsonPath("$.explicacion").value(org.hamcrest.Matchers.containsString("ownerOrLead")));
    }

    @Test
    void coordenadaEnTextoExplicaQueElMapaNoLaPuedeUsar() throws Exception {
        mockMvc.perform(post("/api/v1/parcels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Huerto Roma","location":{"latitude":"norte","longitude":-99.1},"soilType":"arenoso","ownerOrLead":"Ana López"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("FICHA_ILEGIBLE"))
                .andExpect(jsonPath("$.explicacion").value(org.hamcrest.Matchers.containsString("norte")))
                .andExpect(jsonPath("$.explicacion").value(org.hamcrest.Matchers.containsString("número")));
    }

    @Test
    void parcelaInexistenteUsaLaExcepcionDeLaRed() throws Exception {
        String id = "11111111-1111-1111-1111-111111111111";
        when(service.getById(id)).thenThrow(new ParcelaNoRegistradaException(id));

        mockMvc.perform(get("/api/v1/parcels/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("PARCELA_NO_REGISTRADA"))
                .andExpect(jsonPath("$.titulo").value("Esa parcela no está en la red"))
                .andExpect(jsonPath("$.explicacion").value(org.hamcrest.Matchers.containsString(id)))
                .andExpect(jsonPath("$.explicacion").value(org.hamcrest.Matchers.containsString("darla de alta")));
    }
}
