package br.com.joaodddev.rideflow_api.controller;

import br.com.joaodddev.rideflow_api.dto.request.RideRequest;
import br.com.joaodddev.rideflow_api.dto.response.RideResponse;
import br.com.joaodddev.rideflow_api.service.RideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    // ── Passenger endpoints ───────────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<RideResponse> requestRide(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody RideRequest.Create request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rideService.requestRide(userDetails.getUsername(), request));
    }

    @DeleteMapping("/{rideId}/cancel")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<RideResponse> cancelRide(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String rideId
    ) {
        return ResponseEntity.ok(rideService.cancelRide(userDetails.getUsername(), rideId));
    }

    @GetMapping("/my-history")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<Page<RideResponse>> myHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("requestedAt").descending());
        return ResponseEntity.ok(rideService.getPassengerHistory(userDetails.getUsername(), pageable));
    }

    // ── Driver endpoints ──────────────────────────────────────────────────────

    @PutMapping("/{rideId}/accept")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<RideResponse> acceptRide(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String rideId
    ) {
        return ResponseEntity.ok(rideService.acceptRide(userDetails.getUsername(), rideId));
    }

    @PutMapping("/{rideId}/start")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<RideResponse> startRide(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String rideId
    ) {
        return ResponseEntity.ok(rideService.startRide(userDetails.getUsername(), rideId));
    }

    @PutMapping("/{rideId}/complete")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<RideResponse> completeRide(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String rideId,
            @RequestParam double distanceKm
    ) {
        return ResponseEntity.ok(rideService.completeRide(userDetails.getUsername(), rideId, distanceKm));
    }

    @GetMapping("/driver-history")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<Page<RideResponse>> driverHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("requestedAt").descending());
        return ResponseEntity.ok(rideService.getDriverHistory(userDetails.getUsername(), pageable));
    }

    // ── Shared ────────────────────────────────────────────────────────────────

    @GetMapping("/{rideId}")
    public ResponseEntity<RideResponse> getRide(@PathVariable String rideId) {
        return ResponseEntity.ok(rideService.getRide(rideId));
    }
}
