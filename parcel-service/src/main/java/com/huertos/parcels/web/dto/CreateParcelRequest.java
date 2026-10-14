package com.huertos.parcels.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.huertos.parcels.web.validation.FichaParcelaValida;

@FichaParcelaValida
@JsonIgnoreProperties(ignoreUnknown = false)
public class CreateParcelRequest {

    private String name;
    private LocationRequest location;
    private String soilType;
    private String ownerOrLead;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocationRequest getLocation() {
        return location;
    }

    public void setLocation(LocationRequest location) {
        this.location = location;
    }

    public String getSoilType() {
        return soilType;
    }

    public void setSoilType(String soilType) {
        this.soilType = soilType;
    }

    public String getOwnerOrLead() {
        return ownerOrLead;
    }

    public void setOwnerOrLead(String ownerOrLead) {
        this.ownerOrLead = ownerOrLead;
    }
}
