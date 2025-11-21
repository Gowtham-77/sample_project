package com.example.authdemo.service;

public interface RedisService {
    void saveToken(String username, String token, long expirySeconds);
    String getToken(String username);
    void deleteToken(String username);
    void blacklistToken(String token, long expirySeconds);
    boolean isTokenBlacklisted(String token);
}
