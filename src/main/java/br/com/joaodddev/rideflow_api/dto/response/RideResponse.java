package br.com.joaodddev.rideflow_api.dto.response;

import br.com.joaodddev.rideflow_api.model.Ride;
import br.com.joaodddev.rideflow_api.model.RideStatus;
import java.math.BigDecimal;
import java.time.Instant;

public record RideResponse(
        String id,
        String passengerId,
        String driverId,
        double originLat,
        double originLng,
        String originAddress,
        double destinationLat,
        double destinationLng,
        String destinationAddress,
        RideStatus status,
        BigDecimal fare,
        Double distanceKm,
        Integer durationMinutes,
        Instant requestedAt,
        Instant acceptedAt,
        Instant startedAt,
        Instant completedAt
) {
    public static RideResponse from(Ride ride) {
        return new RideResponse(
                ride.getId(),
                ride.getId(),
                ride.getDriverId(),
                ride.getOrigin() != null ? ride.getOrigin().getLatitude() : 0,
                ride.getOrigin() != null ? ride.getOrigin().getLongitude() : 0,
                ride.getOriginAddress(),
                ride.getDestination() != null ? ride.getDestination().getLatitude() : 0,
                ride.getDestination() != null ? ride.getDestination().getLongitude() : 0,
                ride.getDestinationAddress(),
                ride.getStatus(),
                ride.getFare(),
                ride.getDistanceKm(),
                ride.getDurationMinutes(),
                ride.getRequestedAt(),
                ride.getAcceptedAt(),
                ride.getStartedAt(),
                ride.getCompletedAt()
        );
    }
}
