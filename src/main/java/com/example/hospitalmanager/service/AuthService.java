package com.example.hospitalmanager.service;

import com.example.hospitalmanager.dto.AuthResponse;
import com.example.hospitalmanager.dto.LoginRequest;
import com.example.hospitalmanager.dto.RegisterRequest;
import com.example.hospitalmanager.dto.UserResponse;
import com.example.hospitalmanager.exception.ApiException;
import com.example.hospitalmanager.model.AppUser;
import com.example.hospitalmanager.model.Role;
import com.example.hospitalmanager.repository.UserRepository;
import com.example.hospitalmanager.security.JwtService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ApiException(HttpStatus.CONFLICT, "Username already exists");
        }
        if (request.role() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Role is required");
        }

        AppUser user = AppUser.builder()
                .username(request.username())
                .passwordHash(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .role(request.role())
                .build();

        try {
            return toUserResponse(userRepository.save(user));
        } catch (DuplicateKeyException exception) {
            throw new ApiException(HttpStatus.CONFLICT, "Username already exists");
        }
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(), request.password()));
        } catch (AuthenticationException exception) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        if (!authentication.isAuthenticated()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        AppUser user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new ApiException(
                        HttpStatus.UNAUTHORIZED, "Invalid username or password"));
        Role role = user.getRole();
        String token = jwtService.generateToken(
                user.getUsername(), role != null ? role.name() : null);
        return new AuthResponse(token, "Bearer", jwtService.getExpirationMs(), role);
    }

    private UserResponse toUserResponse(AppUser user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getFullName(), user.getRole());
    }
}