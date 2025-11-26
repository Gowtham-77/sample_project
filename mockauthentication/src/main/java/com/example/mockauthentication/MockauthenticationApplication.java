package com.example.mockauthentication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class	
MockauthenticationApplication {

	public static void main(String[] args) {
		SpringApplication.run(MockauthenticationApplication.class, args);
		System.out.println("✅ Application started successfully on http://localhost:8080");
	}
}
