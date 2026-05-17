package br.com.joaodddev.rideflow_api.dto.response;

import java.time.Instant;

public record LocationBroadcast(
        String driverId,
        String rideId,
        double latitude,
        double longitude,
        Instant timestamp
) {}


