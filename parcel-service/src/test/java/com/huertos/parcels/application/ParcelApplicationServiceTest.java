package com.huertos.parcels.application;

import com.huertos.parcels.domain.Parcel;
import com.huertos.parcels.domain.ParcelRepository;
import com.huertos.parcels.domain.ParcelaDuplicadaException;
import com.huertos.parcels.domain.ParcelaNoRegistradaException;
import com.huertos.parcels.domain.IdentificadorDeParcelaInvalidoException;
import com.huertos.parcels.shared.InputSanitizer;
import com.huertos.parcels.web.dto.CreateParcelRequest;
import com.huertos.parcels.web.dto.CurrentStatusResponse;
import com.huertos.parcels.web.dto.LocationRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParcelApplicationServiceTest {

    @Mock
    private ParcelRepository repository;

    @Spy
    private InputSanitizer sanitizer = new InputSanitizer();

    @Mock
    private MonitoringClient monitoringClient;

    @InjectMocks
    private ParcelApplicationService service;

    @Test
    void guardaElNombreYaLimpioYSinEstadoInventado() {
        when(repository.existsByNameNormalized("huerto roma norte")).thenReturn(false);
        when(repository.save(any(Parcel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var respuesta = service.create(ficha("  Huerto   Roma Norte  ", "arcilloso", "  Ana   López "));

        ArgumentCaptor<Parcel> captor = ArgumentCaptor.forClass(Parcel.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Huerto Roma Norte");
        assertThat(captor.getValue().getOwnerOrLead()).isEqualTo("Ana López");
        assertThat(captor.getValue().getNameNormalized()).isEqualTo("huerto roma norte");
        assertThat(respuesta.currentStatus().situation()).isEqualTo("SIN_LECTURA_DE_SENSOR");
        assertThat(respuesta.currentStatus().summary()).contains("primera lectura");
        assertThat(respuesta.soilType()).isEqualTo("arcilloso");
    }

    @Test
    void nombreRepetidoExplicaElChoqueEnCampo() {
        when(repository.existsByNameNormalized("huerto roma")).thenReturn(true);

        assertThatThrownBy(() -> service.create(ficha("Huerto Roma", "limoso", "Ana López")))
                .isInstanceOf(ParcelaDuplicadaException.class)
                .hasMessageContaining("Huerto Roma")
                .hasMessageContaining("voluntarios y las alertas");
        verify(repository, never()).save(any());
    }

    @Test
    void identificadorRaroNoSeBuscaComoSiFueraUnaZona() {
        assertThatThrownBy(() -> service.getById("parcela-norte"))
                .isInstanceOf(IdentificadorDeParcelaInvalidoException.class)
                .hasMessageContaining("parcela-norte")
                .hasMessageContaining("UUID");
        verify(repository, never()).findById(any());
    }

    @Test
    void parcelaAusenteDiceQueHayQueDarlaDeAlta() {
        String id = "11111111-1111-1111-1111-111111111111";
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(id))
                .isInstanceOf(ParcelaNoRegistradaException.class)
                .hasMessageContaining(id)
                .hasMessageContaining("darla de alta");
    }

    @Test
    void elDetalleTraeElEstadoQueRespondioElMonitoreo() {
        String id = "11111111-1111-1111-1111-111111111111";
        Parcel parcel = new Parcel();
        parcel.setId(id);
        parcel.setName("Huerto Roma");
        parcel.setSoilType(com.huertos.parcels.domain.SoilType.ARENOSO);
        parcel.setOwnerOrLead("Ana López");
        parcel.setLocation(new com.huertos.parcels.domain.GeoLocation(19.4, -99.1));
        when(repository.findById(id)).thenReturn(Optional.of(parcel));
        when(monitoringClient.latest(id)).thenReturn(new CurrentStatusResponse(
                true, "MEDICION_VIGENTE", "La medición vigente está en rango favorable para esta parcela.",
                30.0, 22.0, 6.5, 20.0, 12.0, 18.0, 40.0, null
        ));

        var detalle = service.getById("  " + id + "  ");

        assertThat(detalle.currentStatus().summary()).contains("rango favorable");
        assertThat(detalle.soilType()).isEqualTo("arenoso");
    }

    private CreateParcelRequest ficha(String name, String soil, String owner) {
        LocationRequest location = new LocationRequest();
        location.setLatitude(19.4);
        location.setLongitude(-99.16);
        CreateParcelRequest request = new CreateParcelRequest();
        request.setName(name);
        request.setLocation(location);
        request.setSoilType(soil);
        request.setOwnerOrLead(owner);
        return request;
    }
}
