package com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.AuthenticationImp;

import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Authentication.AuthResponse;
import com.theanh.iamservice.IAM_Service_2.Entities.UserEntity;
import com.theanh.iamservice.IAM_Service_2.Entities.UserRole;
import com.theanh.iamservice.IAM_Service_2.Exception.AppException;
import com.theanh.iamservice.IAM_Service_2.Exception.ErrorCode;
import com.theanh.iamservice.IAM_Service_2.Jwts.JwtUtil;
import com.theanh.iamservice.IAM_Service_2.Repositories.UserRepository;
import com.theanh.iamservice.IAM_Service_2.Repositories.UserRoleRepository;
import com.theanh.iamservice.IAM_Service_2.Services.IGoogleAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GoogleAuthService implements IGoogleAuthService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public AuthResponse googleLogin(OAuth2User user) {
        String email = user.getAttribute("email");

        UserEntity userEntity = userRepository.findByEmailAddress(email).orElseGet(() -> createNewUser(user));

        String accessToken = generateToken(() -> jwtUtil.generateAccessToken(userEntity));
        String refreshToken = generateToken(() -> jwtUtil.generateRefreshToken(userEntity));

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private UserEntity createNewUser(OAuth2User user) {
        String email = user.getAttribute("email");

        UserEntity userEntity = UserEntity.builder()
                .username(user.getAttribute("name"))
                .emailAddress(email)
                .firstname(user.getAttribute("given_name"))
                .lastname(user.getAttribute("family_name"))
                .createdBy(email)
                .createdAt(LocalDateTime.now())
                .lastModifiedBy(email)
                .lastModifiedAt(LocalDateTime.now())
                .build();
        userRepository.save(userEntity);

        UserRole userRole = UserRole.builder()
                .userId(userEntity.getId())
                .roleName("USER")
                .build();
        userRoleRepository.save(userRole);

        return userEntity;
    }

    private String generateToken(TokenSupplier tokenSupplier) {
        try {
            return tokenSupplier.generate();
        } catch (Exception e) {
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @FunctionalInterface
    private interface TokenSupplier {
        String generate() throws Exception;
    }
}