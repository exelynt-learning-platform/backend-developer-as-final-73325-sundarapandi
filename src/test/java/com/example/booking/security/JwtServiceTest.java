package com.example.booking.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
    @Test
    void tokenCanBeGeneratedAndValidated() {
        JwtService service = new JwtService(
                "01234567890123456789012345678901",
                60_000);
        UserDetails user = User.withUsername("alice")
                .password("encoded").roles("USER").build();

        String token = service.generateToken(user);

        assertEquals("alice", service.extractUsername(token));
        assertTrue(service.isValid(token, user));
    }
}
