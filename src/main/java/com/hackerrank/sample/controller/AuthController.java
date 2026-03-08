package com.hackerrank.sample.controller;

import com.hackerrank.sample.audit.AuditService;
import com.hackerrank.sample.security.AuthService;
import com.hackerrank.sample.security.AuthRequest;
import com.hackerrank.sample.security.AuthResponse;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private AuditService auditService;
    
    /**
     * Endpoint de login para obtener JWT token
     * POST /auth/login
     * Body: {"username": "testuser", "password": "password123"}
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.authenticate(
                new AuthRequest(request.getUsername(), request.getPassword())
            );
            
            auditService.logOperation(
                request.getUsername(), "AuthService", "POST", "/auth/login",
                "LOGIN", "SUCCESS", "200", "User logged in successfully"
            );
            
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("success", true);
            responseMap.put("message", "Login exitoso");
            responseMap.put("token", response.getToken());
            responseMap.put("username", response.getUsername());
            responseMap.put("role", response.getRole());
            responseMap.put("timestamp", LocalDateTime.now());
            responseMap.put("expiresIn", "1 hora");
            responseMap.put("tokenType", "Bearer");
            
            logger.info("[AUTH] Usuario: {} - Login exitoso - Status: 200", request.getUsername());
            
            return new ResponseEntity<>(responseMap, HttpStatus.OK);
            
        } catch (Exception e) {
            auditService.logOperation(
                request.getUsername(), "AuthService", "POST", "/auth/login",
                "LOGIN", "FAILED", "401", e.getMessage()
            );
            
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            error.put("timestamp", LocalDateTime.now());
            
            logger.error("[AUTH] Usuario: {} - Login fallido: {}", request.getUsername(), e.getMessage());
            
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }
    }
}

@Data
class LoginRequest {
    private String username;
    private String password;
    
    public LoginRequest() {
    }
    
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
