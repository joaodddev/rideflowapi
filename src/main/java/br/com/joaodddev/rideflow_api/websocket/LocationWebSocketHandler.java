package br.com.joaodddev.rideflow_api.websocket;

import br.com.joaodddev.rideflow_api.dto.request.LocationUpdateRequest;
import br.com.joaodddev.rideflow_api.dto.response.LocationBroadcast;
import br.com.joaodddev.rideflow_api.service.DriverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.Instant;

/**
 * Handles real-time location updates from drivers via STOMP/WebSocket.
 *
 * Flow:
 *  1. Driver connects → authenticates via JWT in STOMP CONNECT headers
 *  2. Driver sends:   /app/driver.location  { latitude, longitude, rideId? }
 *  3. Server:
 *     a. Persists new location to MongoDB (for geo queries)
 *     b. Broadcasts to /topic/ride.{rideId}.location  (passenger subscribes)
 *     c. Broadcasts to /topic/drivers.location        (admin dashboard)
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class LocationWebSocketHandler {

    private final DriverService driverService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/driver.location")
    public void handleLocationUpdate(
            @Payload LocationUpdateRequest request,
            SimpMessageHeaderAccessor headerAccessor,
            Principal principal
    ) {
        if (principal == null) {
            log.warn("Unauthenticated WebSocket location update rejected");
            return;
        }

        String userEmail = principal.getName();
        log.debug("Location update from driver {}: ({}, {})", userEmail, request.latitude(), request.longitude());

        // 1. Persist to MongoDB
        driverService.updateLocationByEmail(userEmail, request.latitude(), request.longitude());

        // 2. Build broadcast payload
        LocationBroadcast broadcast = new LocationBroadcast(
                userEmail,
                request.rideId(),
                request.latitude(),
                request.longitude(),
                Instant.now()
        );

        // 3. Broadcast to ride-specific topic (passenger tracks driver)
        if (request.rideId() != null && !request.rideId().isBlank()) {
            messagingTemplate.convertAndSend(
                    "/topic/ride." + request.rideId() + ".location",
                    broadcast
            );
        }

        // 4. Broadcast to global drivers topic (for admin dashboards / heatmaps)
        messagingTemplate.convertAndSend("/topic/drivers.location", broadcast);
    }
}

