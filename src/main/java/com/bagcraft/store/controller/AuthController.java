package com.bagcraft.store.controller;

import com.bagcraft.store.dto.*;
import com.bagcraft.store.exception.BadRequestException;
import com.bagcraft.store.model.Role;
import com.bagcraft.store.model.User;
import com.bagcraft.store.repository.UserRepository;
import com.bagcraft.store.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest req) {
    	
    	System.out.println(passwordEncoder.encode("admin123"));
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .fullName(req.getFullName())
                .phone(req.getPhone())
                .address(req.getAddress())
                .role(Role.CUSTOMER)
                .build();
        userRepository.save(user);

        String token = jwtService.generateToken(
                org.springframework.security.core.userdetails.User.builder()
                        .username(user.getEmail()).password(user.getPassword())
                        .authorities("ROLE_" + user.getRole().name()).build());

        return AuthResponse.builder().token(token).email(user.getEmail())
                .fullName(user.getFullName()).role(user.getRole().name()).build();
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail()).password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name()).build();

        String token = jwtService.generateToken(userDetails);

        return AuthResponse.builder().token(token).email(user.getEmail())
                .fullName(user.getFullName()).role(user.getRole().name()).build();
    }
}
