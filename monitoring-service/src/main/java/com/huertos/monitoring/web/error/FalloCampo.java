package com.huertos.monitoring.web.error;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FalloCampo(String campo, String motivo) {
}
