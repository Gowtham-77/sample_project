package com.example.mockauthentication.service;

import com.example.mockauthentication.model.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION_TIME = 3600 * 1000; // 1 hour

    public String validateUser(LoginRequest request) {
        logger.info("🔐 Validating user login request...");

        try {
            // ✅ Validate input
            if (request.getUsername() == null || request.getPassword() == null ||
                    request.getUsername().isEmpty() || request.getPassword().isEmpty()) {

                logger.warn("⚠️ Missing username or password in request: {}", request);
                return buildError("Missing username or password.");
            }

            logger.debug("👤 Username received: {}", request.getUsername());

            // ✅ Validate credentials
            if ("Naragani".equals(request.getUsername()) && "123456789".equals(request.getPassword())) {
                logger.info("✅ Valid credentials for user '{}'. Generating JWT token...", request.getUsername());

                // Generate JWT token
                String token = generateJwtToken(request.getUsername());

                Map<String, Object> success = new LinkedHashMap<>();
                success.put("token_type", "bearer");
                success.put("token", token);
                success.put("expires_in", 3600);

                logger.info("🎫 JWT token generated successfully for user '{}'", request.getUsername());
                return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(success);
            } else {
                logger.warn("❌ Invalid login attempt for username '{}'", request.getUsername());
                return buildError("Invalid username or password.");
            }

        } catch (Exception e) {
            logger.error("💥 Exception during user validation: {}", e.getMessage(), e);
            return buildError("Internal Server Error.");
        }
    }

    // ✅ Generate JWT Token
    private String generateJwtToken(String username) {
        logger.debug("🪪 Starting JWT token generation for user '{}'", username);

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        String token = Jwts.builder()
                .setSubject(username)
                .claim("role", "USER")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SECRET_KEY)
                .compact();

        logger.debug("🕒 Token generated with expiration at {}", expiryDate);
        return token;
    }

    // ✅ Error response
    private String buildError(String messageDesc) {
        logger.warn("⚠️ Building error response: {}", messageDesc);

        try {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("status", Map.of("message", java.util.List.of(Map.of(
                    "message_TYPE", "BE",
                    "messageCode", "100",
                    "messageDesc", messageDesc
            ))));

            String jsonError = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(error);
            logger.debug("📦 Error JSON response created successfully.");
            return jsonError;

        } catch (Exception e) {
            logger.error("💥 Failed to build structured error JSON: {}", e.getMessage(), e);
            return "{\"status\":{\"message\":[{\"messageDesc\":\"" + messageDesc + "\"}]}}";
        }
    }
}
