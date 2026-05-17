package br.com.joaodddev.rideflow_api.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RideRequest {

    public record Create(
            @NotNull @DecimalMin("-90") @DecimalMax("90")   Double originLat,
            @NotNull @DecimalMin("-180") @DecimalMax("180") Double originLng,
            @NotBlank String originAddress,

            @NotNull @DecimalMin("-90") @DecimalMax("90")   Double destinationLat,
            @NotNull @DecimalMin("-180") @DecimalMax("180") Double destinationLng,
            @NotBlank String destinationAddress
    ) {}

    public record Rate(
            @NotNull @Min(1) @Max(5) Integer rating,
            String comment
    ) {}
}
