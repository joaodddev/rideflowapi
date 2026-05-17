package br.com.joaodddev.rideflow_api.dto.response;

import br.com.joaodddev.rideflow_api.model.Driver;
import br.com.joaodddev.rideflow_api.model.DriverStatus;

public record DriverResponse(
        String id,
        String userId,
        String name,
        String phone,
        String licensePlate,
        String vehicleModel,
        String vehicleColor,
        DriverStatus status,
        Double latitude,
        Double longitude,
        double rating,
        int totalRides
) {
    public static DriverResponse from(Driver driver) {
        Double lat = null, lng = null;
        if (driver.getLocation() != null) {
            lat = driver.getLocation().getLatitude();
            lng = driver.getLocation().getLongitude();
        }
        return new DriverResponse(
                driver.getId(),
                driver.getUserId(),
                driver.getName(),
                driver.getPhone(),
                driver.getLicensePlate(),
                driver.getVehicleModel(),
                driver.getVehicleColor(),
                driver.getStatus(),
                lat, lng,
                driver.getRating(),
                driver.getTotalRides()
        );
    }
}