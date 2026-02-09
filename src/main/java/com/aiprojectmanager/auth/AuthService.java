package com.aiprojectmanager.auth;

import com.aiprojectmanager.security.JwtService;
import com.aiprojectmanager.user.User;
import com.aiprojectmanager.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) throw new IllegalArgumentException("Email already exists");
        User user = userRepository.save(User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build());
        return new AuthDtos.AuthResponse(jwtService.generateToken(user.getEmail()), user.getEmail(), user.getRole());
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        var user = userRepository.findByEmail(request.email()).orElseThrow();
        return new AuthDtos.AuthResponse(jwtService.generateToken(user.getEmail()), user.getEmail(), user.getRole());
    }
}
