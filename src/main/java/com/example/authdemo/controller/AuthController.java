package com.example.authdemo.controller;

import com.example.authdemo.service.AuthService;
import com.example.authdemo.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 🧱 1️⃣ Register new user
    @PostMapping("/register")
    public ApiResponse register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        String role = request.getOrDefault("role", "ROLE_USER");
        return authService.register(username, password, role);
    }


    // 🔐 2️⃣ Authenticate (login)
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody Map<String, String> request) {
        System.out.println("login controller");
        return ResponseEntity.ok(
                authService.authenticate(request.get("username"), request.get("password"))
        );
    }

    // 🚪 3️⃣ Logout user (invalidate access + refresh tokens)
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@RequestHeader("Authorization") String authHeader,
                                              @RequestBody Map<String, String> request) {
        String token = authHeader.substring(7); // Remove "Bearer "
        return ResponseEntity.ok(
                authService.logout(request.get("username"), token)
        );
    }

    // 🔄 4️⃣ Refresh access token using refresh token
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse> refresh(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(
                authService.refreshToken(request.get("username"), request.get("refreshToken"))
        );
    }
}
