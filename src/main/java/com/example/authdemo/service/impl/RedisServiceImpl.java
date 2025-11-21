package com.example.authdemo.service.impl;

import com.example.authdemo.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String TOKEN_PREFIX = "token:";
    private static final String BLACKLIST_PREFIX = "blacklist:";

    // 🔹 Save Access Token to Redis
    @Override
    public void saveToken(String username, String token, long expirySeconds) {
        String key = TOKEN_PREFIX + username;
        log.debug("Saving token for user: {} | key: {} | expiry: {}s", username, key, expirySeconds);
        try {
            redisTemplate.opsForValue().set(key, token, expirySeconds, TimeUnit.SECONDS);
            log.debug("Token saved successfully for user: {}", username);
        } catch (Exception e) {
            log.error("Failed to save token for user {}: {}", username, e.getMessage());
        }
    }

    // 🔹 Get Access Token from Redis
    @Override
    public String getToken(String username) {
        String key = TOKEN_PREFIX + username;
        log.debug("Fetching token for user: {} | key: {}", username, key);
        try {
            Object token = redisTemplate.opsForValue().get(key);
            if (token != null) {
                log.debug("Token retrieved successfully for user: {}", username);
                return token.toString();
            } else {
                log.debug("No token found for user: {}", username);
                return null;
            }
        } catch (Exception e) {
            log.error("Failed to fetch token for user {}: {}", username, e.getMessage());
            return null;
        }
    }

    // 🔹 Delete Token from Redis
    @Override
    public void deleteToken(String username) {
        String key = TOKEN_PREFIX + username;
        log.debug("Deleting token for user: {} | key: {}", username, key);
        try {
            redisTemplate.delete(key);
            log.debug("Token deleted successfully for user: {}", username);
        } catch (Exception e) {
            log.error("Failed to delete token for user {}: {}", username, e.getMessage());
        }
    }

    // 🔹 Add Token to Blacklist
    @Override
    public void blacklistToken(String token, long expirySeconds) {
        String key = BLACKLIST_PREFIX + token;
        log.debug("Blacklisting token: {} | expiry: {}s", key, expirySeconds);
        try {
            redisTemplate.opsForValue().set(key, "BLACKLISTED", expirySeconds, TimeUnit.SECONDS);
            log.debug("Token blacklisted successfully for {} seconds", expirySeconds);
        } catch (Exception e) {
            log.error("Failed to blacklist token: {}", e.getMessage());
        }
    }

    // 🔹 Check if Token is Blacklisted
    @Override
    public boolean isTokenBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        log.debug("Checking if token is blacklisted: {}", key);
        try {
            Boolean exists = redisTemplate.hasKey(key);
            boolean isBlacklisted = Boolean.TRUE.equals(exists);
            log.debug("Token blacklist check for {}: {}", key, isBlacklisted);
            return isBlacklisted;
        } catch (Exception e) {
            log.error("Failed to check blacklist status for token: {}", e.getMessage());
            return false;
        }
    }
}
