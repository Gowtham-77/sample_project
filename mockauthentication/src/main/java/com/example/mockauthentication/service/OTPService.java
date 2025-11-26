package com.example.mockauthentication.service;

import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OTPService {

    private final ConcurrentHashMap<String, String> otpStore = new ConcurrentHashMap<>();

    public String generateOtp(String userId) {
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        otpStore.put(userId, otp);
        return otp;
    }

    public boolean validateOtp(String userId, String otp) {
        return otp.equals(otpStore.get(userId));
    }
}
