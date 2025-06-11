package com.codewithmosh.store.auth;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.codewithmosh.store.users.User;
import com.codewithmosh.store.users.UserRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    
    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal();

        return userRepository.findById(userId).orElseThrow();
    }

    public LoginResponse login(LoginUserRequest request) {
        var user = userRepository.findByEmail(request.email()).orElseThrow();
        
        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        return new LoginResponse(accessToken, refreshToken);
    }

    public Jwt refreshAccessToken(String refreshToken) {
        var jwt = jwtService.parse(refreshToken);
        if (jwt == null || jwt.isExpired()) {
            return null;
        }
        
        var user = userRepository.findById(jwt.getUserId()).orElse(null);
        if (user == null) {
            return null;
        }
        
        return jwtService.generateAccessToken(user);
    }



}
