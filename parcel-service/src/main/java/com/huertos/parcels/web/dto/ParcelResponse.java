package com.huertos.parcels.web.dto;

import java.time.Instant;

public record ParcelResponse(
        String id,
        String name,
        LocationResponse location,
        String soilType,
        String ownerOrLead,
        Instant createdAt,
        CurrentStatusResponse currentStatus
) {
}
