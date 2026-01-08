package com.imdb.backend.controller;

import com.imdb.backend.dto.AuthDTO;
import com.imdb.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthDTO dto) {
        try {
            authService.register(dto);
            return ResponseEntity.ok("User registered successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthDTO dto) {
        try {
            return ResponseEntity.ok(authService.login(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).build();
        }
    }
}
