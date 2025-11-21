package com.example.authdemo.service.impl;

import com.example.authdemo.entity.UserEntity;
import com.example.authdemo.repository.UserRepository;
import com.example.authdemo.service.AuthService;
import com.example.authdemo.service.JwtService;
import com.example.authdemo.service.RedisService;
import com.example.authdemo.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RedisService redisService;

    // ==============================================================
    // 🟢 REGISTER
    // ==============================================================
    @Override
    public ApiResponse register(String username, String password, String role) {
        log.info("Register request received for username: {}", username);
        log.debug("Checking if username '{}' already exists", username);

        if (userRepository.findByUsername(username).isPresent()) {
            log.warn("Registration failed: username '{}' already exists", username);
            return new ApiResponse(false, "User already exists", null);
        }

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));

        // ✅ Assign role
        if (role != null && role.equalsIgnoreCase("ROLE_ADMIN")) {
            user.setRole("ROLE_ADMIN");
        } else {
            user.setRole("ROLE_USER");
        }

        log.debug("Saving new user '{}' with role '{}' to database", username, user.getRole());
        userRepository.save(user);

        log.info("User '{}' registered successfully with role '{}'", username, user.getRole());
        return new ApiResponse(true, "User registered successfully", null);
    }

    // ==============================================================
    // 🟢 AUTHENTICATE / LOGIN
    // ==============================================================
    @Override
    public ApiResponse authenticate(String username, String password) {
        log.info("Authenticating user: {}", username);

        Optional<UserEntity> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            log.warn("Authentication failed: username '{}' not found", username);
            return new ApiResponse(false, "Invalid username", null);
        }

        UserEntity user = userOpt.get();
        log.debug("User '{}' found in database. Verifying password...", username);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Authentication failed: invalid password for user '{}'", username);
            return new ApiResponse(false, "Invalid password", null);
        }

        log.debug("Password verified. Generating JWT tokens for '{}'", username);
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        log.debug("Access token generated: {}", accessToken);
        log.debug("Refresh token generated: {}", refreshToken);

        redisService.saveToken(username, accessToken, jwtService.getExpirationInSeconds());
        log.debug("Access token cached in Redis for user '{}'", username);

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);

        log.info("User '{}' authenticated successfully", username);
        return new ApiResponse(true, "Login successful", response);
    }

    // ==============================================================
    // 🟢 REFRESH TOKEN
    // ==============================================================
    @Override
    public ApiResponse refreshToken(String username, String refreshToken) {
        log.info("Token refresh request for user '{}'", username);
        log.debug("Validating refresh token for '{}'", username);

        if (!jwtService.isRefreshTokenValid(refreshToken, username)) {
            log.warn("Invalid refresh token for user '{}'", username);
            return new ApiResponse(false, "Invalid refresh token", null);
        }

        Optional<UserEntity> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            log.warn("Refresh failed: user '{}' not found in database", username);
            return new ApiResponse(false, "User not found", null);
        }

        UserEntity user = userOpt.get();
        log.debug("Refresh token valid. Generating new access token for '{}'", username);

        String newAccessToken = jwtService.generateAccessToken(user);
        redisService.saveToken(username, newAccessToken, jwtService.getExpirationInSeconds());

        log.debug("New access token stored in Redis for '{}'", username);

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", newAccessToken);

        log.info("Access token refreshed successfully for '{}'", username);
        return new ApiResponse(true, "Access token refreshed successfully", response);
    }

    // ==============================================================
    // 🟢 LOGOUT
    // ==============================================================
    @Override
    public ApiResponse logout(String username, String token) {
        log.info("Logout request for user '{}'", username);
        log.debug("Blacklisting token in Redis for '{}'", username);

        redisService.blacklistToken(token, jwtService.getExpirationInSeconds());
        redisService.deleteToken(username);

        log.info("User '{}' logged out successfully", username);
        return new ApiResponse(true, "User logged out successfully", null);
    }
}
