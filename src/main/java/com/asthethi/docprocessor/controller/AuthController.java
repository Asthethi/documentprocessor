package com.asthethi.docprocessor.controller;

import com.asthethi.docprocessor.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtService  jwtService;

    // Hard Credentials for demo
    private static final String DEMO_USERNAME = "admin";
    private static final String DEMO_PASSWORD = "password";

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        // Validate the credentials (replace with db lookup in production)
        if(DEMO_USERNAME.equals(username) && DEMO_PASSWORD.equals(password)) {
            String token = jwtService.generateToken(username);
            return ResponseEntity.ok(Map.of("token", token, "type", "Bearer","message", "Login Successful"));
        }

        return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
    }

}
