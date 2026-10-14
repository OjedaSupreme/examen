package com.huertos.parcels.shared;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InputSanitizerTest {

    private final InputSanitizer sanitizer = new InputSanitizer();

    @Test
    void colapsaEspaciosYNormalizaUnicode() {
        assertThat(sanitizer.sanitize("\uFF28uerto   Norte")).isEqualTo("Huerto Norte");
    }

    @Test
    void retiraEtiquetasYCaracteresDeControl() {
        assertThat(sanitizer.sanitize("Huerto <b>Norte</b>\u0007")).isEqualTo("Huerto Norte");
    }

    @Test
    void detectaScriptYOperadorDeConsulta() {
        assertThat(sanitizer.contieneContenidoBloqueado("Huerto <script>alert(1)</script>")).isTrue();
        assertThat(sanitizer.contieneContenidoBloqueado("{$ne: null}")).isTrue();
        assertThat(sanitizer.contieneContenidoBloqueado("Parcela Norte 2")).isFalse();
    }

    @Test
    void reconoceUuidDeParcela() {
        assertThat(sanitizer.esUuid("11111111-1111-1111-1111-111111111111")).isTrue();
        assertThat(sanitizer.esUuid("parcela-norte")).isFalse();
    }
}
