package com.theanh.iamservice.IAM_Service_2.Config;

import com.theanh.iamservice.IAM_Service_2.Jwts.JwtFilter;
import com.theanh.iamservice.IAM_Service_2.Jwts.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    private final CustomPermissionEvaluator customPermissionEvaluator;

    @Value("${keycloak.enabled}")
    private boolean isKeycloakEnabled;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizationManagerRequest
                        -> authorizationManagerRequest
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/auth/sign-in",
                                "/api/auth/sign-up",
                                "/api/profile/password/forgot",
                                "/api/files/public/**",
                                "/api/files/excel/**"
                        )
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .oauth2Login(oauth2
                        -> oauth2.defaultSuccessUrl("/api/auth/home", true));

        if (isKeycloakEnabled) {
            http
                    .oauth2ResourceServer(oauth2
                            -> oauth2.jwt(Customizer.withDefaults()));
        } else {
            http
                    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        }

        return http.build();
    }

    @Bean
    public MethodSecurityExpressionHandler expressionHandler() {
        var expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(customPermissionEvaluator);
        return expressionHandler;
    }
}
