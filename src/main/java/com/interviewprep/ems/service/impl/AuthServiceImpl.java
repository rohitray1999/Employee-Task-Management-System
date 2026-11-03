package com.interviewprep.ems.service.impl;

import com.interviewprep.ems.config.JwtService;
import com.interviewprep.ems.dto.request.LoginRequest;
import com.interviewprep.ems.dto.request.RegisterRequest;
import com.interviewprep.ems.dto.response.AuthResponse;
import com.interviewprep.ems.entity.User;
import com.interviewprep.ems.enums.Role;
import com.interviewprep.ems.exception.DuplicateResourceException;
import com.interviewprep.ems.repository.UserRepository;
import com.interviewprep.ems.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already taken: " + request.getUsername());
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword())) // NEVER store raw passwords
                .role(Role.ROLE_USER)
                .enabled(true)
                .build();
        userRepository.save(user);

        String token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // Delegates credential checking to Spring Security's AuthenticationManager
        // (which uses our DaoAuthenticationProvider + BCrypt under the hood)
        // instead of comparing passwords ourselves.
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        UserDetails user = (UserDetails) auth.getPrincipal();
        String token = jwtService.generateToken(user);

        String role = user.getAuthorities().stream().findFirst()
                .map(Object::toString).orElse("ROLE_USER");

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .username(user.getUsername())
                .role(role)
                .build();
    }
}
