package com.huertos.parcels.web.error;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FalloCampo(String campo, String motivo) {
}
