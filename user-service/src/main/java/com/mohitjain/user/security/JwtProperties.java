package com.mohitjain.user.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    private String secret;

    private long accessTokenExpiry = 900000;

    private long refreshTokenExpiry = 604800000;

    private String issuer = "spring-microservices-starter";
}
