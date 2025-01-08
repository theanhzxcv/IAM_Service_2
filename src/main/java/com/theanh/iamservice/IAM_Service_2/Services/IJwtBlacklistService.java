package com.theanh.iamservice.IAM_Service_2.Services;

public interface IJwtBlacklistService {

    void blacklistedAccessToken(String accessToken, long expirationDuration);

    void blacklistedRefreshToken(String refreshToken, long expirationDuration);

    boolean isTokenBlacklisted(String token);
}
