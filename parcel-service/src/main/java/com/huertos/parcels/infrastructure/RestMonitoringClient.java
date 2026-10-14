package com.huertos.parcels.infrastructure;

import com.huertos.parcels.application.MonitoringClient;
import com.huertos.parcels.web.dto.CurrentStatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class RestMonitoringClient implements MonitoringClient {

    private static final Logger log = LoggerFactory.getLogger(RestMonitoringClient.class);

    private final RestClient monitoringRestClient;

    public RestMonitoringClient(RestClient monitoringRestClient) {
        this.monitoringRestClient = monitoringRestClient;
    }

    @Override
    public CurrentStatusResponse latest(String parcelId) {
        try {
            CurrentStatusResponse body = monitoringRestClient.get()
                    .uri("/api/v1/conditions/latest/{parcelId}", parcelId)
                    .retrieve()
                    .body(CurrentStatusResponse.class);
            if (body == null || body.summary() == null) {
                log.warn("El monitoreo respondió vacío para la parcela {}", parcelId);
                return CurrentStatusResponse.monitoreoCaido();
            }
            return body;
        } catch (RestClientException ex) {
            log.warn("El monitoreo no respondió para la parcela {}: {}", parcelId, ex.getMessage());
            return CurrentStatusResponse.monitoreoCaido();
        }
    }
}
