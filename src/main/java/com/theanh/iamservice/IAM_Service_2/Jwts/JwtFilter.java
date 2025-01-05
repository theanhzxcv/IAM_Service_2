package com.theanh.iamservice.IAM_Service_2.Jwts;

import com.theanh.iamservice.IAM_Service_2.Exception.ErrorCode;
import com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.Blacklist.JwtBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final JwtBlacklistService jwtBlacklistService;

    @Value("${keycloak.enabled}")
    private boolean isKeycloakEnabled;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        if (token == null || jwtBlacklistService.isTokenBlacklisted(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String email = extractEmailAndValidateToken(token, request);
        if (email == null) {
            filterChain.doFilter(request, response);
            return;
        }

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
        if (isTokenValid(token, userDetails)) {
            setAuthentication(userDetails, request);
        }

//        logSecurityContextDetails();
        filterChain.doFilter(request, response);
    }

    private String extractEmailAndValidateToken(String token, HttpServletRequest request) {
        String email;
        if (isKeycloakEnabled) {
            email = jwtUtil.extractEmailFrKeycloakJwt(token);
            if (email == null || SecurityContextHolder.getContext().getAuthentication() != null) {
                return null;
            }
        } else {
            email = jwtUtil.extractEmailFrSystemJwt(token);
            if (email == null || SecurityContextHolder.getContext().getAuthentication() != null) {
                return null;
            }
        }
        return email;
    }

    private boolean isTokenValid(String token, UserDetails userDetails) {
        if (isKeycloakEnabled) {
            return jwtUtil.isKeycloakTokenValid(token, userDetails);
        } else {
            return jwtUtil.isSystemTokenValid(token, userDetails);
        }
    }

    private void setAuthentication(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
