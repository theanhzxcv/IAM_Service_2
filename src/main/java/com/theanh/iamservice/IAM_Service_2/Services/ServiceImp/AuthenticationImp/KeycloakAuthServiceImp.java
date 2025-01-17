package com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.AuthenticationImp;

import com.theanh.iamservice.IAM_Service_2.AuditorAware.AuditorAwareImp;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Authentication.SignInRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Authentication.SignOutRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Authentication.SignUpRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Authentication.VerificationRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Authentication.AuthResponse;
import com.theanh.iamservice.IAM_Service_2.Entities.UserActivityEntity;
import com.theanh.iamservice.IAM_Service_2.Entities.UserEntity;
import com.theanh.iamservice.IAM_Service_2.Entities.UserRole;
import com.theanh.iamservice.IAM_Service_2.Exception.AppException;
import com.theanh.iamservice.IAM_Service_2.Exception.ErrorCode;
import com.theanh.iamservice.IAM_Service_2.Jwts.JwtUtil;
import com.theanh.iamservice.IAM_Service_2.Keycloak.KeycloakProperties;
import com.theanh.iamservice.IAM_Service_2.Mappers.UserMapper;
import com.theanh.iamservice.IAM_Service_2.Repositories.UserActivityRepository;
import com.theanh.iamservice.IAM_Service_2.Repositories.UserRepository;
import com.theanh.iamservice.IAM_Service_2.Repositories.UserRoleRepository;
import com.theanh.iamservice.IAM_Service_2.Services.IAuthService;
import com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.BlacklistImp.JwtBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditorAwareImp auditorAwareImp;
    private final UserRoleRepository userRoleRepository;
    private final KeycloakProperties keycloakProperties;
    private final JwtBlacklistService jwtBlacklistService;
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

        MultiValueMap<String, String> loginRequestBody = createLoginRequestBody(signInRequest);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, Object> responseBody = sendLoginRequest(loginUrl, loginRequestBody, headers);
        String accessToken = (String) responseBody.get("access_token");
        String refreshToken = (String) responseBody.get("refresh_token");

        logUserActivity(signInRequest.getEmailAddress(), request, "Sign In");

        return buildAuthResponse(accessToken, refreshToken);
    }

    private MultiValueMap<String, String> createLoginRequestBody(SignInRequest signInRequest) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", keycloakProperties.getClientId());
        body.add("client_secret", keycloakProperties.getClientSecret());
        body.add("username", signInRequest.getEmailAddress());
        body.add("password", signInRequest.getPassword());
        return body;
    }

    private Map<String, Object> sendLoginRequest(String url, MultiValueMap<String, String> body, HttpHeaders headers) {
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new AppException(ErrorCode.AUTHENTICATION_FAILED);
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 401) {
                throw new AppException(ErrorCode.AUTHENTICATION_FAILED);
            }
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private void logUserActivity(String emailAddress, HttpServletRequest request, String activity) {
        UserActivityEntity userActivityEntity = UserActivityEntity
                .builder()
                .ipAddress(getUserIp(request))
                .emailAddress(emailAddress)
                .activity(activity)
                .logAt(LocalDateTime.now())
                .build();

        userActivityRepository.save(userActivityEntity);
    }

    private AuthResponse buildAuthResponse(String accessToken, String refreshToken) {
        return AuthResponse
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
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

        MultiValueMap<String, Object> userPayload = createUserPayload(signUpRequest);
        registerUserInKeycloak(adminToken, registerUrl, userPayload);

        UserEntity userEntity = saveUserInDatabase(signUpRequest);
        assignDefaultRole(userEntity);
        logUserActivity(signUpRequest, request);

        return "Sign up successfully! Welcome, " + signUpRequest.getLastname()
                + " " + signUpRequest.getFirstname() + "!";
    }

    private MultiValueMap<String, Object> createUserPayload(SignUpRequest signUpRequest) {
        MultiValueMap<String, Object> userPayload = new LinkedMultiValueMap<>();
        userPayload.add("username", signUpRequest.getUsername());
        userPayload.add("email", signUpRequest.getEmailAddress());
        userPayload.add("firstName", signUpRequest.getFirstname());
        userPayload.add("lastName", signUpRequest.getLastname());
        userPayload.add("enabled", true);

        Map<String, Object> credentials = new HashMap<>();
        credentials.put("type", "password");
        credentials.put("value", signUpRequest.getPassword());
        credentials.put("temporary", false);
        userPayload.add("credentials", List.of(credentials));

        return userPayload;
    }

    private void registerUserInKeycloak(String adminToken, String registerUrl, MultiValueMap<String, Object> userPayload) {
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
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 409) {
                throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }
            throw new AppException(ErrorCode.REGISTRATION_FAILED);
        }
    }

    private UserEntity saveUserInDatabase(SignUpRequest signUpRequest) {
        UserEntity userEntity = userMapper.toUserEntity(signUpRequest);
        userEntity.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        userEntity.setCreatedBy(auditorAwareImp.getCurrentAuditor().orElse("Unknown"));
        userEntity.setCreatedAt(LocalDateTime.now());
        userEntity.setLastModifiedBy(auditorAwareImp.getCurrentAuditor().orElse("Unknown"));
        userEntity.setLastModifiedAt(LocalDateTime.now());
        userRepository.save(userEntity);

        return userEntity;
    }

    private void assignDefaultRole(UserEntity userEntity) {
        UserRole userRole = UserRole.builder()
                .userId(userEntity.getId())
                .roleName("USER")
                .build();
        userRoleRepository.save(userRole);
    }

    private void logUserActivity(SignUpRequest signUpRequest, HttpServletRequest request) {
        UserActivityEntity userActivityEntity = UserActivityEntity
                .builder()
                .ipAddress(getUserIp(request))
                .emailAddress(signUpRequest.getEmailAddress())
                .activity("Sign Up")
                .logAt(LocalDateTime.now())
                .build();
        userActivityRepository.save(userActivityEntity);
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

    //error
    @Override
    public String logout(SignOutRequest signOutRequest, HttpServletRequest request) {
        String logoutUrl = keycloakProperties.getAuthServerUrl()
                + "/realms/"
                + keycloakProperties.getRealm()
                + "/protocol/openid-connect/logout";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", keycloakProperties.getClientId());
        body.add("client_secret", keycloakProperties.getClientSecret());
        body.add("refresh_token", signOutRequest.getRefreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        try {
            restTemplate.exchange(
                    logoutUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Void.class
            );
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new AppException(ErrorCode.AUTHENTICATION_FAILED);
            } else {
                throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
        return "Log out successfully, See you later!";
    }
}
