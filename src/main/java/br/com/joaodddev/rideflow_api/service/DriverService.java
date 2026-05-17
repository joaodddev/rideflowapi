package br.com.joaodddev.rideflow_api.service;

import br.com.joaodddev.rideflow_api.dto.response.DriverResponse;
import br.com.joaodddev.rideflow_api.exception.BusinessException;
import br.com.joaodddev.rideflow_api.exception.ResourceNotFoundException;
import br.com.joaodddev.rideflow_api.model.Driver;
import br.com.joaodddev.rideflow_api.model.GeoJsonPoint;
import br.com.joaodddev.rideflow_api.model.User;
import br.com.joaodddev.rideflow_api.repository.DriverRepository;
import br.com.joaodddev.rideflow_api.repository.UserRepository;
import br.com.joaodddev.rideflow_api.model.DriverStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverService {

    private static final double DEFAULT_RADIUS_KM = 5.0;

    private final DriverRepository driverRepository;
    private final UserRepository userRepository;

    public DriverResponse registerDriver(String userEmail, String licensePlate,
                                         String vehicleModel, String vehicleColor) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        if (driverRepository.findByUserId(user.getId()).isPresent()) {
            throw new BusinessException("Driver profile already exists for this user");
        }

        Driver driver = Driver.builder()
                .userId(user.getId())
                .name(user.getName())
                .phone(user.getPhone())
                .licensePlate(licensePlate)
                .vehicleModel(vehicleModel)
                .vehicleColor(vehicleColor)
                .status(DriverStatus.OFFLINE)
                .rating(5.0)
                .totalRides(0)
                .build();

        return DriverResponse.from(driverRepository.save(driver));
    }

    public void updateLocationByEmail(String userEmail, double latitude, double longitude) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        Driver driver = driverRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found for user: " + userEmail));

        driver.setLocation(new GeoJsonPoint(longitude, latitude));
        driverRepository.save(driver);
        log.debug("Location updated for driver {}: ({}, {})", driver.getId(), latitude, longitude);
    }

    public DriverResponse updateStatus(String userEmail, DriverStatus status) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        Driver driver = driverRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver profile not found"));

        driver.setStatus(status);
        return DriverResponse.from(driverRepository.save(driver));
    }

    public List<DriverResponse> findNearbyDrivers(double latitude, double longitude, double radiusKm) {
        Point point = new Point(longitude, latitude);
        Distance distance = new Distance(radiusKm, Metrics.KILOMETERS);

        return driverRepository
                .findByLocationNearAndStatus(point, distance, DriverStatus.AVAILABLE)
                .stream()
                .map(DriverResponse::from)
                .toList();
    }

    public List<DriverResponse> findNearbyDrivers(double latitude, double longitude) {
        return findNearbyDrivers(latitude, longitude, DEFAULT_RADIUS_KM);
    }

    public DriverResponse getDriverByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        return driverRepository.findByUserId(user.getId())
                .map(DriverResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found for user: " + email));
    }

    public DriverResponse getDriver(String driverId) {
        return driverRepository.findById(driverId)
                .map(DriverResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found: " + driverId));
    }

    Driver findDriverEntityById(String driverId) {
        return driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found: " + driverId));
    }

    Driver findDriverEntityByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        return driverRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found for user: " + email));
    }

    void saveDriver(Driver driver) {
        driverRepository.save(driver);
    }
}
