package com.aiprojectmanager.auth;

import com.aiprojectmanager.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthDtos.AuthResponse>> register(@Valid @RequestBody AuthDtos.RegisterRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.ok(service.register(request), "Registered"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthDtos.AuthResponse>> login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(service.login(request), "Logged in"));
    }
}
