package com.mln.service;

import com.mln.dto.AuthResponse;
import com.mln.dto.LoginRequest;
import com.mln.dto.RegisterRequest;
import com.mln.entity.User;
import com.mln.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        // Create new user - no password validation, just store username
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(""); // No password needed for fake auth
        user.setDisplayName(request.getDisplayName());
        
        user = userRepository.save(user);
        
        // Return user info with UUID (no token needed)
        return new AuthResponse(
            user.getId(),
            user.getUsername(),
            user.getDisplayName(),
            "" // No token
        );
    }
    
    public AuthResponse login(LoginRequest request) {
        // Simple fake authentication - just find user by username
        // No password verification needed
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Return user info with UUID (no token needed)
        return new AuthResponse(
            user.getId(),
            user.getUsername(),
            user.getDisplayName(),
            "" // No token
        );
    }
    
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}

