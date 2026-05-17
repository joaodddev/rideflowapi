package br.com.joaodddev.rideflow_api.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        String userId,
        String email,
        String name,
        String role
) {
    public static AuthResponse of(String accessToken, String refreshToken,
                                  String userId, String email, String name, String role) {
        return new AuthResponse(accessToken, refreshToken, "Bearer", userId, email, name, role);
    }
}
