package br.com.joaodddev.rideflow_api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "drivers")
public class Driver {

    @Id
    private String id;
    private String userId;
    private String name;
    private String phone;
    private String licensePlate;
    private String vehicleModel;
    private String vehicleColor;

    @Builder.Default
    private DriverStatus status = DriverStatus.OFFLINE;

    /**
     * GeoJSON Point for MongoDB geospatial queries ($nearSphere, $geoWithin).
     * Format: { type: "Point", coordinates: [longitude, latitude] }
     */
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;

    private double rating;

    private int totalRides;

    @LastModifiedDate
    private Instant lastSeen;
    // Let Lombok generate correct, typed getters/setters. The previous manual
    // implementations used Object types and incorrect conversions and were removed.
}
