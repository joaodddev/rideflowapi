package br.com.joaodddev.rideflow_api.controller;

import br.com.joaodddev.rideflow_api.dto.response.DriverResponse;
import br.com.joaodddev.rideflow_api.model.DriverStatus;
import br.com.joaodddev.rideflow_api.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<DriverResponse> register(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String licensePlate,
            @RequestParam String vehicleModel,
            @RequestParam String vehicleColor
    ) {
        return ResponseEntity.ok(
                driverService.registerDriver(userDetails.getUsername(), licensePlate, vehicleModel, vehicleColor)
        );
    }

    @PutMapping("/status")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<DriverResponse> updateStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam DriverStatus status
    ) {
        return ResponseEntity.ok(driverService.updateStatus(userDetails.getUsername(), status));
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<DriverResponse>> findNearby(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "5.0") double radius
    ) {
        return ResponseEntity.ok(driverService.findNearbyDrivers(lat, lng, radius));
    }

    @GetMapping("/{driverId}")
    public ResponseEntity<DriverResponse> getDriver(@PathVariable String driverId) {
        return ResponseEntity.ok(driverService.getDriver(driverId));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<DriverResponse> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(driverService.getDriverByEmail(userDetails.getUsername()));
    }
}
