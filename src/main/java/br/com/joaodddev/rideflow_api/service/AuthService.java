package br.com.joaodddev.rideflow_api.service;

import br.com.joaodddev.rideflow_api.dto.request.AuthRequest;
import br.com.joaodddev.rideflow_api.dto.response.AuthResponse;
import br.com.joaodddev.rideflow_api.exception.BusinessException;
import br.com.joaodddev.rideflow_api.model.Role;
import br.com.joaodddev.rideflow_api.model.User;
import br.com.joaodddev.rideflow_api.repository.UserRepository;
import br.com.joaodddev.rideflow_api.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final UserDetailsService userDetailsService;

    public AuthResponse register(AuthRequest.Register request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email already registered: " + request.email());
        }

        Role role = parseRole(request.role());

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .phone(request.phone())
                .roles(Set.of(role))
                .build();

        user = userRepository.save(user);
        log.info("New user registered: {} [{}]", user.getEmail(), role);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return AuthResponse.of(accessToken, refreshToken, user.getId(), user.getEmail(), user.getName(), role.name());
    }

    public AuthResponse login(AuthRequest.Login request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException("User not found"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        String role = user.getRoles().stream().findFirst().map(Role::name).orElse("PASSENGER");
        return AuthResponse.of(accessToken, refreshToken, user.getId(), user.getEmail(), user.getName(), role);
    }

    public AuthResponse refreshToken(AuthRequest.RefreshToken request) {
        String email = jwtService.extractUsername(request.refreshToken());
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (!jwtService.isTokenValid(request.refreshToken(), userDetails)) {
            throw new BusinessException("Invalid or expired refresh token");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found"));

        String newAccessToken = jwtService.generateToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);
        String role = user.getRoles().stream().findFirst().map(Role::name).orElse("PASSENGER");

        return AuthResponse.of(newAccessToken, newRefreshToken, user.getId(), user.getEmail(), user.getName(), role);
    }

    private Role parseRole(String roleStr) {
        if (roleStr == null) return Role.PASSENGER;
        try {
            return Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Role.PASSENGER;
        }
    }
}
