package br.com.joaodddev.rideflow_api.service;

import br.com.joaodddev.rideflow_api.dto.request.RideRequest;
import br.com.joaodddev.rideflow_api.dto.response.RideResponse;
import br.com.joaodddev.rideflow_api.exception.BusinessException;
import br.com.joaodddev.rideflow_api.exception.ResourceNotFoundException;
import br.com.joaodddev.rideflow_api.model.*;
import br.com.joaodddev.rideflow_api.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RideService {

    private static final double FARE_BASE = 3.50;
    private static final double FARE_PER_KM = 1.75;

    private final RideRepository rideRepository;
    private final DriverService driverService;
    private final SimpMessagingTemplate messagingTemplate;

    // ── Passenger actions ─────────────────────────────────────────────────────

    public RideResponse requestRide(String passengerEmail, RideRequest.Create request) {
        rideRepository.findByPassengerIdAndStatus(passengerEmail, RideStatus.REQUESTED)
                .ifPresent(r -> { throw new BusinessException("You already have a pending ride request"); });

        Ride ride = Ride.builder()
                .passengerId(passengerEmail)
                .origin(new GeoJsonPoint(request.originLng(), request.originLat()))
                .originAddress(request.originAddress())
                .destination(new GeoJsonPoint(request.destinationLng(), request.destinationLat()))
                .destinationAddress(request.destinationAddress())
                .status(RideStatus.REQUESTED)
                .build();

        ride = rideRepository.save(ride);
        log.info("Ride {} requested by {}", ride.getId(), passengerEmail);

        messagingTemplate.convertAndSend("/topic/rides.new", RideResponse.from(ride));
        return RideResponse.from(ride);
    }

    public RideResponse cancelRide(String passengerEmail, String rideId) {
        Ride ride = findRide(rideId);

        if (!passengerEmail.equals(ride.getPassengerId())) {
            throw new BusinessException("Not authorized for this ride");
        }

        if (!List.of(RideStatus.REQUESTED, RideStatus.ACCEPTED).contains(ride.getStatus())) {
            throw new BusinessException("Cannot cancel a ride in status: " + ride.getStatus());
        }

        ride.setStatus(RideStatus.CANCELLED);
        ride.setCancelledAt(Instant.now());

        if (ride.getDriverId() != null) {
            Driver driver = driverService.findDriverEntityById(ride.getDriverId());
            driver.setStatus(DriverStatus.AVAILABLE);
            driverService.saveDriver(driver);
        }

        ride = rideRepository.save(ride);
        broadcastRideUpdate(ride);
        return RideResponse.from(ride);
    }

    // ── Driver actions ────────────────────────────────────────────────────────

    public RideResponse acceptRide(String driverEmail, String rideId) {
        Ride ride = findRide(rideId);

        if (ride.getStatus() != RideStatus.REQUESTED) {
            throw new BusinessException("Ride is no longer available");
        }

        Driver driver = driverService.findDriverEntityByEmail(driverEmail);

        if (driver.getStatus() != DriverStatus.AVAILABLE) {
            throw new BusinessException("Driver is not available");
        }

        ride.setDriverId(driver.getId());
        ride.setStatus(RideStatus.ACCEPTED);
        ride.setAcceptedAt(Instant.now());

        driver.setStatus(DriverStatus.ON_RIDE);
        driverService.saveDriver(driver);

        ride = rideRepository.save(ride);
        broadcastRideUpdate(ride);
        log.info("Ride {} accepted by driver {}", ride.getId(), driver.getId());
        return RideResponse.from(ride);
    }

    public RideResponse startRide(String driverEmail, String rideId) {
        Ride ride = findRide(rideId);
        assertDriverOwnsRide(ride, driverEmail);

        if (ride.getStatus() != RideStatus.ACCEPTED) {
            throw new BusinessException("Ride must be ACCEPTED before starting");
        }

        ride.setStatus(RideStatus.IN_PROGRESS);
        ride.setStartedAt(Instant.now());

        ride = rideRepository.save(ride);
        broadcastRideUpdate(ride);
        return RideResponse.from(ride);
    }

    public RideResponse completeRide(String driverEmail, String rideId, double distanceKm) {
        Ride ride = findRide(rideId);
        assertDriverOwnsRide(ride, driverEmail);

        if (ride.getStatus() != RideStatus.IN_PROGRESS) {
            throw new BusinessException("Ride is not in progress");
        }

        Instant now = Instant.now();
        int durationMinutes = (int) ChronoUnit.MINUTES.between(ride.getStartedAt(), now);

        ride.setStatus(RideStatus.COMPLETED);
        ride.setCompletedAt(now);
        ride.setDistanceKm(distanceKm);
        ride.setDurationMinutes(durationMinutes);
        ride.setFare(calculateFare(distanceKm));

        Driver driver = driverService.findDriverEntityById(ride.getDriverId());
        driver.setStatus(DriverStatus.AVAILABLE);
        driver.setTotalRides(driver.getTotalRides() + 1);
        driverService.saveDriver(driver);

        ride = rideRepository.save(ride);
        broadcastRideUpdate(ride);
        log.info("Ride {} completed — {}km, R${}", ride.getId(), distanceKm, ride.getFare());
        return RideResponse.from(ride);
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    public RideResponse getRide(String rideId) {
        return RideResponse.from(findRide(rideId));
    }

    public Page<RideResponse> getPassengerHistory(String passengerEmail, Pageable pageable) {
        return rideRepository.findByPassengerId(passengerEmail, pageable).map(RideResponse::from);
    }

    public Page<RideResponse> getDriverHistory(String driverEmail, Pageable pageable) {
        Driver driver = driverService.findDriverEntityByEmail(driverEmail);
        return rideRepository.findByDriverId(driver.getId(), pageable).map(RideResponse::from);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Ride findRide(String rideId) {
        return rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found: " + rideId));
    }

    private void assertDriverOwnsRide(Ride ride, String driverEmail) {
        Driver driver = driverService.findDriverEntityByEmail(driverEmail);
        if (!driver.getId().equals(ride.getDriverId())) {
            throw new BusinessException("Not authorized for this ride");
        }
    }

    private BigDecimal calculateFare(double distanceKm) {
        double fare = FARE_BASE + (distanceKm * FARE_PER_KM);
        return BigDecimal.valueOf(fare).setScale(2, RoundingMode.HALF_UP);
    }

    private void broadcastRideUpdate(Ride ride) {
        RideResponse response = RideResponse.from(ride);
        messagingTemplate.convertAndSend("/topic/ride." + ride.getId(), response);
        messagingTemplate.convertAndSendToUser(ride.getPassengerId(), "/queue/rides", response);
    }
}