package br.com.joaodddev.rideflow_api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "rides")
public class Ride {

    @Id
    private String id;

    @Indexed
    private String passengerId;

    @Indexed
    private String driverId;
    private GeoJsonPoint origin;
    private String originAddress;
    private GeoJsonPoint destination;
    private String destinationAddress;

    @Builder.Default
    private RideStatus status = RideStatus.REQUESTED;
    private BigDecimal fare;
    private Double distanceKm;
    private Integer durationMinutes;
    private Integer passengerRating;
    private Integer driverRating;
    private String notes;

    @CreatedDate
    private Instant requestedAt;
    private Instant acceptedAt;
    private Instant startedAt;
    private Instant completedAt;
    private Instant cancelledAt;

    @LastModifiedDate
    private Instant updatedAt;
    // Removed manual accessor methods that contained incorrect implementations.
    // Lombok will generate properly typed getters/setters and builder.
}
