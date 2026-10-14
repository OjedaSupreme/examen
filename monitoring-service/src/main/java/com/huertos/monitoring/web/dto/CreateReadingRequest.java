package com.huertos.monitoring.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.huertos.monitoring.web.validation.MedicionValida;

@MedicionValida
@JsonIgnoreProperties(ignoreUnknown = false)
public class CreateReadingRequest {

    private String parcelId;
    private Double soilMoisture;
    private Double temperature;
    private Double ph;
    private Double nitrogen;
    private Double phosphorus;
    private Double potassium;
    private Double airQualityIndex;

    public String getParcelId() {
        return parcelId;
    }

    public void setParcelId(String parcelId) {
        this.parcelId = parcelId;
    }

    public Double getSoilMoisture() {
        return soilMoisture;
    }

    public void setSoilMoisture(Double soilMoisture) {
        this.soilMoisture = soilMoisture;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getPh() {
        return ph;
    }

    public void setPh(Double ph) {
        this.ph = ph;
    }

    public Double getNitrogen() {
        return nitrogen;
    }

    public void setNitrogen(Double nitrogen) {
        this.nitrogen = nitrogen;
    }

    public Double getPhosphorus() {
        return phosphorus;
    }

    public void setPhosphorus(Double phosphorus) {
        this.phosphorus = phosphorus;
    }

    public Double getPotassium() {
        return potassium;
    }

    public void setPotassium(Double potassium) {
        this.potassium = potassium;
    }

    public Double getAirQualityIndex() {
        return airQualityIndex;
    }

    public void setAirQualityIndex(Double airQualityIndex) {
        this.airQualityIndex = airQualityIndex;
    }
}
