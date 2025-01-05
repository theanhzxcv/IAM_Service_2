package com.theanh.iamservice.IAM_Service_2.Jwts;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

@Component
public class KeycloakJwt {
    @Value("${spring.security.oauth2.resource-server.jwt.jwk-set-uri}")
    private String jwkSetUri;

    public JwtDecoder decoder() {
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }
}
