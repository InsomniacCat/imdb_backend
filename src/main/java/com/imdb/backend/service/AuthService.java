package com.imdb.backend.service;

import com.imdb.backend.dto.AuthDTO;
import com.imdb.backend.entity.User;
import com.imdb.backend.repository.UserRepository;
import com.imdb.backend.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepo, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public void register(AuthDTO dto) {
        if (userRepo.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        User user = new User(dto.getUsername(), passwordEncoder.encode(dto.getPassword()));
        userRepo.save(user);
    }

    public Map<String, String> login(AuthDTO dto) {
        User user = userRepo.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        Map<String, String> res = new HashMap<>();
        res.put("token", token);
        res.put("username", user.getUsername());
        return res;
    }
}
