package com.theanh.iamservice.IAM_Service_2.Jwts;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final JwtDecoder jwtDecoder;
    private final RSAKeysUtil rsaKeysUtil;
    private final static long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 30;
    private final static long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 5;

    public String generateToken(UserDetails userDetails, long expirationTime) throws Exception {
        PrivateKey privateKey = rsaKeysUtil.getPrivateKey();
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .setId(UUID.randomUUID().toString())
//                .claim("role", buildScope(userDetails))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public String generateAccessToken(UserDetails userDetails) throws Exception {
        return generateToken(userDetails, ACCESS_TOKEN_EXPIRATION);
    }

    public String generateRefreshToken(UserDetails userDetails) throws Exception {
        return generateToken(userDetails, REFRESH_TOKEN_EXPIRATION);
    }

    public Claims extractClaims(String token) {
        try {
            PublicKey publicKey = rsaKeysUtil.getPublicKey();

            return Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String extractEmailFrSystemJwt(String token) {
        try {
            return extractClaims(token).getSubject();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Date getSystemJwtExpirationTime(String token) {
        try {
            return extractClaims(token).getExpiration();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String extractEmailFrKeycloakJwt(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return jwt.getClaim("email");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Date getKeycloakJwtExpirationTime(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            Instant expiration = jwt.getExpiresAt();
            assert expiration != null;
            return Date.from(expiration);
        } catch (JwtException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isSystemTokenExpired(String token) {
        try {
            return extractClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private boolean isKeycloakTokenExpired(String token) {
        try {
            return Date.from(jwtDecoder.decode(token).getExpiresAt()).before(new Date());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean isSystemTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmailFrSystemJwt(token);
        return (email.equals(userDetails.getUsername()) && !isSystemTokenExpired(token));
    }

    public boolean isKeycloakTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmailFrKeycloakJwt(token);
        return (email.equals(userDetails.getUsername()) && !isKeycloakTokenExpired(token));
    }
}
