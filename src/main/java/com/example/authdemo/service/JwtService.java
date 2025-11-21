package com.example.authdemo.service;

import com.example.authdemo.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

    // 🔹 Generate Access Token (short-lived)
    String generateAccessToken(UserEntity user);

    // 🔹 Generate Refresh Token (longer-lived)
    String generateRefreshToken(UserEntity user);

    // 🔹 Extract Username from token
    String extractUsername(String token);

    // 🔹 Extract Role from token
    String extractRole(String token);

    // 🔹 Validate Access Token
    boolean isTokenValid(String token, UserDetails userDetails);

    // 🔹 Validate Refresh Token
    boolean isRefreshTokenValid(String refreshToken, String username);

    // 🔹 Get token expiry duration (used for Redis TTL)
    long getExpirationInSeconds();
}
