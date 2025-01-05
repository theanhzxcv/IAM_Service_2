package com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.AuthenticationImp;

import com.theanh.iamservice.IAM_Service_2.AuditorAware.AuditorAwareImp;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Authentication.SignInRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Authentication.SignOutRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Authentication.SignUpRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Request.Authentication.VerificationRequest;
import com.theanh.iamservice.IAM_Service_2.Dtos.Response.Authentication.AuthResponse;
import com.theanh.iamservice.IAM_Service_2.Entities.UserActivityEntity;
import com.theanh.iamservice.IAM_Service_2.Entities.UserEntity;
import com.theanh.iamservice.IAM_Service_2.Exception.AppException;
import com.theanh.iamservice.IAM_Service_2.Exception.ErrorCode;
import com.theanh.iamservice.IAM_Service_2.Jwts.JwtUtil;
import com.theanh.iamservice.IAM_Service_2.Mapper.UserEntityMapper;
import com.theanh.iamservice.IAM_Service_2.Repositories.UserActivityRepository;
import com.theanh.iamservice.IAM_Service_2.Repositories.UserRepository;
import com.theanh.iamservice.IAM_Service_2.Services.IAuthService;
import com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.Blacklist.JwtBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class ApplicationAuthServiceImp implements IAuthService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final AuditorAwareImp auditorAwareImp;
    private final PasswordEncoder passwordEncoder;
    private final UserEntityMapper userEntityMapper;
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

        if (userEntity.isBanned() || userEntity.isDeleted()) {
            throw new AppException(ErrorCode.USER_DEACTIVATED);
        }

        if (!passwordEncoder.matches(signInRequest.getPassword(), userEntity.getPassword())) {
            throw new AppException(ErrorCode.AUTHENTICATION_FAILED);
        }

        try {
            String accessToken = jwtUtil.generateAccessToken(userEntity);
            String refreshToken = jwtUtil.generateRefreshToken(userEntity);

            UserActivityEntity userActivityEntity = UserActivityEntity
                    .builder()
                    .ipAddress(getUserIp(request))
                    .emailAddress(signInRequest.getEmailAddress())
                    .activity("Sign In")
                    .logAt(LocalDateTime.now())
                    .build();

            userActivityRepository.save(userActivityEntity);

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (Exception e) {
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String registration(SignUpRequest signUpRequest, HttpServletRequest request) {
        if (userRepository.findByEmailAddress(signUpRequest.getEmailAddress()).isPresent()) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        try {
            UserEntity userEntity = userEntityMapper.toUserEntity(signUpRequest);
            userEntity.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

            userEntity.setCreatedBy(auditorAwareImp.getCurrentAuditor().orElse("Unknown"));
            userEntity.setCreatedAt(LocalDateTime.now());

            userEntity.setLastModifiedBy(auditorAwareImp.getCurrentAuditor().orElse("Unknown"));
            userEntity.setLastModifiedAt(LocalDateTime.now());

            userRepository.save(userEntity);

            UserActivityEntity userActivityEntity = UserActivityEntity
                    .builder()
                    .ipAddress(getUserIp(request))
                    .emailAddress(signUpRequest.getEmailAddress())
                    .activity("Sign Up")
                    .logAt(LocalDateTime.now())
                    .build();

            userActivityRepository.save(userActivityEntity);
        } catch (Exception e) {
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return "Sign up successfully! Welcome, " + signUpRequest.getLastname()
                + " " + signUpRequest.getFirstname() + "!";
    }

    @Override
    public AuthResponse verification(VerificationRequest verificationRequest) {
        return null;
    }

    @Override
    public String logout(SignOutRequest signOutRequest, HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        if (jwtBlacklistService.isTokenBlacklisted(signOutRequest.getRefreshToken())) {
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        final String accessToken = authHeader.substring(7);

        Date accessTokenExpiration = jwtUtil.getSystemJwtExpirationTime(accessToken);
        long accessTokenExpirationDuration = accessTokenExpiration.getTime() - System.currentTimeMillis();
        jwtBlacklistService.blacklistedAccessToken(accessToken, accessTokenExpirationDuration);

        Date refreshTokenExpiration = jwtUtil.getSystemJwtExpirationTime(signOutRequest.getRefreshToken());
        long refreshTokenExpirationDuration = refreshTokenExpiration.getTime() - System.currentTimeMillis();
        jwtBlacklistService.blacklistedRefreshToken(signOutRequest.getRefreshToken(), refreshTokenExpirationDuration);

        return "Log out successful";
    }
}
