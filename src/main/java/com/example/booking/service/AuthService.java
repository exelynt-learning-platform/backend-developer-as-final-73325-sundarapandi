package com.example.booking.service;

import com.example.booking.dto.*;
import com.example.booking.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(principal);
        String role = principal.getAuthorities().stream()
                .findFirst().map(a -> a.getAuthority().replace("ROLE_", "")).orElse("");
        return new LoginResponse(token, "Bearer", principal.getUsername(), role);
    }
}
