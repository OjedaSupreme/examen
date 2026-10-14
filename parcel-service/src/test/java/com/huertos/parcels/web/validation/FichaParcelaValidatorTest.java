package com.huertos.parcels.web.validation;

import com.huertos.parcels.shared.MensajesParcela;
import com.huertos.parcels.web.dto.CreateParcelRequest;
import com.huertos.parcels.web.dto.LocationRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FichaParcelaValidatorTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void nombreVacioExplicaQueLaZonaLlegoSinNombre() {
        var fallos = validator.validate(request("   ", 19.4, -99.1, "arcilloso", "Ana López"));

        assertThat(fallos).anySatisfy(fallo -> {
            assertThat(fallo.getPropertyPath().toString()).isEqualTo("name");
            assertThat(fallo.getMessage()).isEqualTo(MensajesParcela.NOMBRE_AUSENTE);
        });
    }

    @Test
    void latitudFueraDeRangoCitaElNumeroRecibido() {
        var fallos = validator.validate(request("Huerto Roma", 190, -99.1, "limoso", "Ana López"));

        assertThat(fallos).anySatisfy(fallo -> {
            assertThat(fallo.getPropertyPath().toString()).contains("latitude");
            assertThat(fallo.getMessage()).contains("190").contains("entre -90 y 90");
        });
    }

    @Test
    void sueloAjenoDiceCualesSiTrabajaLaRed() {
        var fallos = validator.validate(request("Huerto Roma", 19.4, -99.1, "pedregoso", "Ana López"));

        assertThat(fallos).anySatisfy(fallo -> {
            assertThat(fallo.getPropertyPath().toString()).isEqualTo("soilType");
            assertThat(fallo.getMessage()).contains("pedregoso").contains("arcillosas, limosas o arenosas");
        });
    }

    @Test
    void scriptEnElNombreNoSeTrataComoUnErrorGenerico() {
        var fallos = validator.validate(request("Huerto <script>x</script>", 19.4, -99.1, "arenoso", "Ana López"));

        assertThat(fallos).anySatisfy(fallo ->
                assertThat(fallo.getMessage()).contains("etiquetas, scripts o operadores de consulta")
        );
    }

    @Test
    void fichaLimpiaNoProduceFallos() {
        assertThat(validator.validate(request("  Parcela   Norte 2  ", 19.4194, -99.162, " ARCILLOSO ", "  Ana   López ")))
                .isEmpty();
    }

    private CreateParcelRequest request(String name, double latitude, double longitude, String soil, String owner) {
        LocationRequest location = new LocationRequest();
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        CreateParcelRequest request = new CreateParcelRequest();
        request.setName(name);
        request.setLocation(location);
        request.setSoilType(soil);
        request.setOwnerOrLead(owner);
        return request;
    }
}
