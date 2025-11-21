package com.example.authdemo.service;

import com.example.authdemo.util.ApiResponse;

public interface AuthService {
    ApiResponse register(String username, String password,String role);
    ApiResponse authenticate(String username, String password);
    ApiResponse refreshToken(String username, String refreshToken);
    ApiResponse logout(String username, String token);
}
