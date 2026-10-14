package com.huertos.parcels.application;

import com.huertos.parcels.web.dto.CurrentStatusResponse;

public interface MonitoringClient {

    CurrentStatusResponse latest(String parcelId);
}
