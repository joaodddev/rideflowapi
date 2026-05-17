package br.com.joaodddev.rideflow_api.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record LocationUpdateRequest(
        @NotNull @DecimalMin("-90")  @DecimalMax("90")  Double latitude,
        @NotNull @DecimalMin("-180") @DecimalMax("180") Double longitude,
        String rideId   // optional — filled when driver is on a ride
) {}
