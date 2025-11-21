package com.example.authdemo.service.impl;
import com.example.authdemo.entity.UserEntity;
import com.example.authdemo.service.JwtService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @Value("${jwt.refreshExpiration}")
    private long refreshExpirationMs;

    // 🔹 Get the signing key
    private Key getSignKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // 🔹 Generate Access Token
    @Override
    public String generateAccessToken(UserEntity user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("role", user.getRole()) // ensure it includes "ROLE_ADMIN" or "ROLE_USER"
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }



    // 🔹 Generate Refresh Token
    @Override
    public String generateRefreshToken(UserEntity user) {
        return buildToken(new HashMap<>(), user.getUsername(), refreshExpirationMs);
    }

    // 🔹 Common method to build token
    private String buildToken(Map<String, Object> claims, String subject, long expirationMs) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 🔹 Extract Username
    @Override
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // 🔹 Extract Role
    @Override
    public String extractRole(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get("role", String.class);
        } catch (Exception e) {
            log.error("Error extracting role from JWT: {}", e.getMessage());
            return null;
        }
    }

    // 🔹 Validate Token
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // 🔹 Check Token Expiry
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    // 🔹 Parse Claims
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 🔹 For Redis TTL usage
    @Override
    public long getExpirationInSeconds() {
        return jwtExpirationMs / 1000;
    }

    // 🔹 Validate Refresh Token
    @Override
    public boolean isRefreshTokenValid(String token, String username) {
        try {
            return (extractUsername(token).equals(username) && !isTokenExpired(token));
        } catch (Exception e) {
            log.error("Invalid refresh token: {}", e.getMessage());
            return false;
        }
    }
}
