package com.example.authdemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

    @RestController
    @RequestMapping("/api/user")
    public class UserController {

        @GetMapping("/profile")
        public String getUserProfile() {
            return "👤 Hello USER! You are authorized to access your profile.";
        }
    }


