package com.example.documentflowapi.controller;

import com.example.documentflowapi.model.AuthResponse;
import com.example.documentflowapi.model.LoginRequest;
import com.example.documentflowapi.service.JwtService;
import com.example.documentflowapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return userRepository.findByUsername(request.getUsername())
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .map(user -> ResponseEntity.ok(new AuthResponse(jwtService.generateToken(user.getUsername()))))
                .orElse(ResponseEntity.status(401).build());
    }
}