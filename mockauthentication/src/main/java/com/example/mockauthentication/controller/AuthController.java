package com.example.mockauthentication.controller;

import com.example.mockauthentication.service.JWTService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private JWTService jwtService;

    // Static OTP value
    private static final String STATIC_OTP = "123456";

    // 🔑 LOGIN Endpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        logger.info("🔐 Login attempt received for username: {}", username);

        if ("Naragani".equals(username) && "123456789".equals(password)) {
            logger.info("✅ Login successful for user: {}", username);

            String token = jwtService.generateToken(username);
            logger.debug("🎟️ JWT Token generated for {}: {}", username, token);

            return ResponseEntity.ok(Map.of(
                    "token_type", "bearer",
                    "token", token,
                    "expires_in", 3600
            ));
        }

        logger.warn("⚠️ Invalid login attempt for username: {}", username);

        return ResponseEntity.status(401).body(Map.of(
                "error", "Invalid credentials"
        ));
    }

    // 📌 OTP Endpoint (static OTP — no token required)
    @PostMapping("/sendotp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
        String bankId = request.get("bankId");
        String userId = request.get("userId");
        String txnType = request.get("txnType");

        logger.info("📩 OTP request received — bankId: {}, userId: {}, txnType: {}", bankId, userId, txnType);

        // ❌ Validate bank and user
        if (!"abc1".equalsIgnoreCase(bankId) || !"TEST2".equalsIgnoreCase(userId)) {
            logger.warn("🚫 Unauthorized OTP request for bankId={} userId={}", bankId, userId);
            return ResponseEntity.status(403).body(
                    Map.of(
                            "status", Map.of(
                                    "message", new Object[]{
                                            Map.of(
                                                    "message_TYPE", "BE",
                                                    "messageCode", "100",
                                                    "messageDesc", "The user is not authorized to perform the action."
                                            )
                                    }
                            )
                    )
            );
        }

        logger.info("🔢 Static OTP returned → {}", STATIC_OTP);

        return ResponseEntity.ok(Map.of(
                "otp", STATIC_OTP,
                "message", "OTP sent successfully"
        ));
    }

    // 👤 PROFILE Endpoint
    @GetMapping("/profile")
    public ResponseEntity<?> profile() {
        logger.info("👤 Accessed protected endpoint: /api/profile");

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Welcome Naragani! ✅ You have accessed a protected endpoint."
        ));
    }
}
