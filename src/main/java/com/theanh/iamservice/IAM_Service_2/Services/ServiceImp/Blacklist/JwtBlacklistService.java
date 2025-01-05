package com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.Blacklist;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class JwtBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;

    public void blacklistedAccessToken(String accessToken, long expirationDuration) {
        redisTemplate.opsForValue().set(accessToken, "blacklisted_accessToken", expirationDuration, TimeUnit.MILLISECONDS);
    }

    public void blacklistedRefreshToken(String refreshToken, long expirationDuration) {
        redisTemplate.opsForValue().set(refreshToken, "blacklisted_refreshToken", expirationDuration, TimeUnit.MILLISECONDS);
    }

    public boolean isTokenBlacklisted(String token) {
        return redisTemplate.hasKey(token);
    }
}
