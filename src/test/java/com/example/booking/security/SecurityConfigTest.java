package com.example.booking.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;

class SecurityConfigTest {
    @Test
    void passwordIsBcryptHashed() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String raw = "User@123";
        String hash = encoder.encode(raw);

        assertNotEquals(raw, hash);
        assertTrue(encoder.matches(raw, hash));
    }
}
