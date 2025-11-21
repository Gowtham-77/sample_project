package com.example.authdemo.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    private boolean success;
    private String message;
    private Object data;
}


//Entity	UserEntity — "stores user info like username, password, role"
//Repository -	UserRepository — performs CRUD on the users table
//Service Layer	Business logic: registration, login, token management
//Security Layer -	"Handles authentication, authorization, and JWT validation"
//Redis	Stores blacklisted tokens or temporary data
//PostgreSQL	Permanent user data storage