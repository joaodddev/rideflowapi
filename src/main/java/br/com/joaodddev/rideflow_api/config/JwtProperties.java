package br.com.joaodddev.rideflow_api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "rideflow.jwt")
public class JwtProperties {
    private String secret;
    private long expirationMs;
    private long refreshExpirationMs;

    public long getRefreshExpirationMs() {
        return 0;
    }

    public long getExpirationMs() {
        return 0;
    }

    public String getSecret() {
        return null;
    }
}