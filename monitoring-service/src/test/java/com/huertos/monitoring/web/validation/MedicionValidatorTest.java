package com.huertos.monitoring.web.validation;

import com.huertos.monitoring.web.dto.CreateReadingRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MedicionValidatorTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void phImposibleExplicaLaEscalaDeSuelo() {
        CreateReadingRequest request = lecturaValida();
        request.setPh(18.0);

        var fallos = validator.validate(request);

        assertThat(fallos).anySatisfy(fallo -> {
            assertThat(fallo.getPropertyPath().toString()).isEqualTo("ph");
            assertThat(fallo.getMessage()).contains("18").contains("0 a 14");
        });
    }

    @Test
    void humedadAusenteDiceQueNoSePuedeAvisarElRiego() {
        CreateReadingRequest request = lecturaValida();
        request.setSoilMoisture(null);

        var fallos = validator.validate(request);

        assertThat(fallos).anySatisfy(fallo ->
                assertThat(fallo.getMessage()).contains("sin humedad del suelo").contains("riego")
        );
    }

    @Test
    void codigoQueNoEsUuidExplicaQueFaltaLaZona() {
        CreateReadingRequest request = lecturaValida();
        request.setParcelId("huerto-roma");

        var fallos = validator.validate(request);

        assertThat(fallos).anySatisfy(fallo ->
                assertThat(fallo.getMessage()).contains("huerto-roma").contains("UUID")
        );
    }

    private CreateReadingRequest lecturaValida() {
        CreateReadingRequest request = new CreateReadingRequest();
        request.setParcelId("11111111-1111-1111-1111-111111111111");
        request.setSoilMoisture(35.0);
        request.setTemperature(22.0);
        request.setPh(6.7);
        request.setNitrogen(20.0);
        request.setPhosphorus(14.0);
        request.setPotassium(18.0);
        request.setAirQualityIndex(40.0);
        return request;
    }
}
