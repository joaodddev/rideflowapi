package br.com.joaodddev.rideflow_api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GeoJSON Point representation for MongoDB geospatial indexing.
 * MongoDB requires coordinates as [longitude, latitude] (not lat/lon).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeoJsonPoint {

    private String type = "Point";

    /**
     * [longitude, latitude] — MongoDB GeoJSON order.
     */
    private double[] coordinates;

    public GeoJsonPoint(double longitude, double latitude) {
        this.type = "Point";
        this.coordinates = new double[]{longitude, latitude};
    }

    public double getLongitude() {
        return coordinates[0];
    }

    public double getLatitude() {
        return coordinates[1];
    }
}