package br.com.joaodddev.rideflow_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthRequest {

    public record Register(
            @NotBlank String name,
            @Email @NotBlank String email,
            @NotBlank @Size(min = 8) String password,
            String phone,
            String role   // "PASSENGER" | "DRIVER" — defaults to PASSENGER if null
    ) {}

    public record Login(
            @Email @NotBlank String email,
            @NotBlank String password
    ) {}

    public record RefreshToken(
            @NotBlank String refreshToken
    ) {}
}
