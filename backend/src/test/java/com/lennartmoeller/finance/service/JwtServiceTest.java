package com.lennartmoeller.finance.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtServiceTest {
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        java.lang.reflect.Field f = null;
        try {
            f = JwtService.class.getDeclaredField("secret");
            f.setAccessible(true);
            f.set(jwtService, "testsecretsecretsecretsec");
            f = JwtService.class.getDeclaredField("expirationMs");
            f.setAccessible(true);
            f.set(jwtService, 3600000L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGenerateAndValidateToken() {
        String token = jwtService.generateToken("user");
        assertNotNull(token);
        String username = jwtService.validateAndGetUsername(token);
        assertEquals("user", username);
    }
}
