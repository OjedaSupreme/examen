package com.huertos.monitoring.infrastructure;

import com.huertos.monitoring.application.ParcelCatalog;
import com.huertos.monitoring.domain.ParcelaSinAltaException;
import com.huertos.monitoring.domain.ServicioParcelasNoDisponibleException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class RestParcelCatalog implements ParcelCatalog {

    private final RestClient parcelRestClient;

    public RestParcelCatalog(RestClient parcelRestClient) {
        this.parcelRestClient = parcelRestClient;
    }

    @Override
    public void assertExists(String parcelId) {
        try {
            parcelRestClient.get()
                    .uri("/api/v1/parcels/{id}", parcelId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ParcelaSinAltaException(parcelId);
        } catch (RestClientException ex) {
            throw new ServicioParcelasNoDisponibleException(ex);
        }
    }
}
