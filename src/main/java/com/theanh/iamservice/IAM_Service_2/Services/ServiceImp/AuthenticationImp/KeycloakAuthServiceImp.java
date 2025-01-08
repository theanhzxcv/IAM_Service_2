package com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.AuthenticationImp;

import com.theanh.iamservice.IAM_Service_2.AuditorAware.AuditorAwareImp;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Authentication.SignInRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Authentication.SignOutRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Authentication.SignUpRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Authentication.VerificationRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Authentication.AuthResponse;
import com.theanh.iamservice.IAM_Service_2.Entities.RoleEntity;
import com.theanh.iamservice.IAM_Service_2.Entities.UserActivityEntity;
import com.theanh.iamservice.IAM_Service_2.Entities.UserEntity;
import com.theanh.iamservice.IAM_Service_2.Entities.UserRole;
import com.theanh.iamservice.IAM_Service_2.Exception.AppException;
import com.theanh.iamservice.IAM_Service_2.Exception.ErrorCode;
import com.theanh.iamservice.IAM_Service_2.Keycloak.KeycloakProperties;
import com.theanh.iamservice.IAM_Service_2.Mappers.UserMapper;
import com.theanh.iamservice.IAM_Service_2.Repositories.RoleRepository;
import com.theanh.iamservice.IAM_Service_2.Repositories.UserActivityRepository;
import com.theanh.iamservice.IAM_Service_2.Repositories.UserRepository;
import com.theanh.iamservice.IAM_Service_2.Repositories.UserRoleRepository;
import com.theanh.iamservice.IAM_Service_2.Services.IAuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class KeycloakAuthServiceImp implements IAuthService {
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditorAwareImp auditorAwareImp;
    private final UserMapper userMapper;
    private final KeycloakProperties keycloakProperties;
    private final UserActivityRepository userActivityRepository;

    public String getUserIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0];
        }
        return ipAddress;
    }

    @Override
    public AuthResponse login(SignInRequest signInRequest, HttpServletRequest request) {
        UserEntity userEntity = userRepository.findByEmailAddress(signInRequest.getEmailAddress())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (userEntity.isDeleted() || userEntity.isBanned()) {
            throw new AppException(ErrorCode.USER_DEACTIVATED);
        }

        String loginUrl = keycloakProperties.getAuthServerUrl()
                + "/realms/"
                + keycloakProperties.getRealm()
                + "/protocol/openid-connect/token";

        Map<String, String> body = new HashMap<>();
        body.put("grant_type", "password");
        body.put("client_id", keycloakProperties.getClientId());
        body.put("client_secret", keycloakProperties.getClientSecret());
        body.put("username", signInRequest.getEmailAddress());
        body.put("password", signInRequest.getPassword());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String formBody = body.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce((a, b) -> a + "&" + b)
                .orElse("");

        HttpEntity<String> requestEntity = new HttpEntity<>(formBody, headers);
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    loginUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Map.class);

            Map<String, Object> responseBody = response.getBody();

            if (response.getStatusCode().is2xxSuccessful()) {
                String accessToken = (String) responseBody.get("access_token");
                String refreshToken = (String) responseBody.get("refresh_token");

                UserActivityEntity userActivityEntity = UserActivityEntity
                        .builder()
                        .ipAddress(getUserIp(request))
                        .emailAddress(signInRequest.getEmailAddress())
                        .activity("Sign In")
                        .logAt(LocalDateTime.now())
                        .build();

                userActivityRepository.save(userActivityEntity);

                return AuthResponse
                        .builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            } else {
                throw new RuntimeException("Unexpected error occurred");
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 401) {
                throw new AppException(ErrorCode.AUTHENTICATION_FAILED);
            }
            throw new AppException(ErrorCode.AUTHENTICATION_FAILED);
        } catch (Exception e) {
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private String getAdminToken() {
        String tokenUrl =
                keycloakProperties.getAuthServerUrl()
                + "/realms/"
                + keycloakProperties.getRealm()
                + "/protocol/openid-connect/token";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", keycloakProperties.getClientId());
        body.add("client_secret", keycloakProperties.getClientSecret());
        body.add("username", keycloakProperties.getAdminUsername());
        body.add("password", keycloakProperties.getAdminPassword());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(
                tokenUrl,
                new org.springframework.http.HttpEntity<>(body, headers),
                Map.class
        );

        return (String) response.getBody().get("access_token");
    }

    @Override
    public String registration(SignUpRequest signUpRequest, HttpServletRequest request) {
        if (userRepository.findByEmailAddress(signUpRequest.getEmailAddress()).isPresent()
                || userRepository.findByUsername(signUpRequest.getUsername()).isPresent()) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        String adminToken = getAdminToken();
        String registerUrl =
                keycloakProperties.getAuthServerUrl()
                + "/admin/realms/"
                + keycloakProperties.getRealm()
                + "/users";

        Map<String, Object> userPayload = Map.of(
                "username", signUpRequest.getUsername(),
                "email", signUpRequest.getEmailAddress(),
                "firstName", signUpRequest.getFirstname(),
                "lastName", signUpRequest.getLastname(),
                "enabled", true,
                "credentials", List.of(
                        Map.of(
                                "type", "password",
                                "value", signUpRequest.getPassword(),
                                "temporary", false
                        )
                )
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);
        try {
            ResponseEntity<String> responseBody = restTemplate.postForEntity(
                    registerUrl,
                    new HttpEntity<>(userPayload, headers),
                    String.class
            );

            if (!responseBody.getStatusCode().is2xxSuccessful()) {
                throw new AppException(ErrorCode.REGISTRATION_FAILED);
            }

            UserEntity userEntity = userMapper.toUserEntity(signUpRequest);
            userEntity.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

            userEntity.setCreatedBy(auditorAwareImp.getCurrentAuditor().orElse("Unknown"));
            userEntity.setCreatedAt(LocalDateTime.now());
            userEntity.setLastModifiedBy(auditorAwareImp.getCurrentAuditor().orElse("Unknown"));
            userEntity.setLastModifiedAt(LocalDateTime.now());
            userRepository.save(userEntity);

            UserRole userRole = UserRole.builder()
                    .userId(userEntity.getId())
                    .roleName("USER")
                    .build();
            userRoleRepository.save(userRole);

            UserActivityEntity userActivityEntity = UserActivityEntity
                    .builder()
                    .ipAddress(getUserIp(request))
                    .emailAddress(signUpRequest.getEmailAddress())
                    .activity("Sign Up")
                    .logAt(LocalDateTime.now())
                    .build();

            userActivityRepository.save(userActivityEntity);

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 409) {
                throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }
        }
        return "Sign up successfully! Welcome, " + signUpRequest.getLastname()
                + " " + signUpRequest.getFirstname() + "!";
    }

    @Override
    public AuthResponse verification(VerificationRequest verificationRequest) {
        return null;
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        String tokenUrl = keycloakProperties.getAuthServerUrl()
                + "/realms/"
                + keycloakProperties.getRealm()
                + "/protocol/openid-connect/token";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("refresh_token", refreshToken);
        body.add("client_id", keycloakProperties.getClientId());
        body.add("client_secret", keycloakProperties.getClientSecret());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, String> responseBody = response.getBody();
            return AuthResponse
                    .builder()
                    .accessToken(responseBody.get("access_token"))
                    .refreshToken(responseBody.get("refresh_token"))
                    .build();
        } else {
            throw new RuntimeException("Failed to refresh token from Keycloak.");
        }
    }

    @Override
    public String logout(SignOutRequest signOutRequest, HttpServletRequest request) {
        String postLogoutRedirectUri = "http://localhost:8081";
        String keycloakLogoutUrl =
                keycloakProperties.getAuthServerUrl()
                + "/realms/"
                + keycloakProperties.getRealm()
                + "/protocol/openid-connect/logout";

        if (signOutRequest.getRefreshToken() != null) {
            revokeRefreshToken(signOutRequest.getRefreshToken());
        }

        Jwt jwt = (Jwt) SecurityContextHolder
                        .getContext().getAuthentication().getPrincipal();

        String idToken = (String) jwt.getTokenValue();

        String logoutUrl =
                keycloakLogoutUrl
                + "?post_logout_redirect_uri="
                + postLogoutRedirectUri
                + "/&id_token_hint"
                + idToken;

        restTemplate.postForObject(logoutUrl, null, String.class);

        SecurityContextHolder.clearContext();

        return "Logged out successfully, refresh token revoked";
    }

    private void revokeRefreshToken(String refreshToken) {
        String keycloakRevokeUrl =
                keycloakProperties.getAuthServerUrl()
                        + "/realms/"
                        + keycloakProperties.getRealm()
                        + "/protocol/openid-connect/revoke";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(keycloakProperties.getClientId(),
                keycloakProperties.getClientSecret()); // Use client credentials for authentication
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); // Set content type

        // Create the request body
        String body = "token=" + refreshToken + "&token_type_hint=refresh_token";

        // Create the request entity
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        // Send the POST request
        restTemplate.postForEntity(keycloakRevokeUrl, request, String.class);
    }
}
