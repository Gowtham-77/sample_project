package com.example.mockauthentication.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JWTService {

    private static final Logger logger = LoggerFactory.getLogger(JWTService.class);

    // ✅ Generate a proper key instead of plain string
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
            "my-super-secret-key-for-jwt-256-bit-security".getBytes()
    );

    // ✅ Generate JWT Token
    public String generateToken(String username) {
        logger.info("🎟️ Generating JWT token for user: {}", username);
        try {
            String token = Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 3600 * 1000)) // 1 hour
                    .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                    .compact();

            logger.info("✅ JWT token successfully generated for user: {}", username);
            return token;

        } catch (Exception e) {
            logger.error("❌ Error while generating JWT token for user {}: {}", username, e.getMessage());
            throw e;
        }
    }

    // ✅ Validate & Parse Token — used in Filter
    public Claims validateToken(String token) {
        logger.debug("🔍 Validating JWT token...");
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            logger.info("✅ JWT token validated successfully for user: {}", claims.getSubject());
            return claims;

        } catch (Exception e) {
            logger.error("❌ Invalid JWT token: {}", e.getMessage());
            throw e;
        }
    }

    // ✅ Optional: Extract username directly
    public String extractUsername(String token) {
        logger.debug("👤 Extracting username from JWT token...");
        String username = validateToken(token).getSubject();
        logger.info("📛 Extracted username from token: {}", username);
        return username;
    }
}
