package com.lennartmoeller.finance.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.lennartmoeller.finance.dto.AuthResponse;
import com.lennartmoeller.finance.dto.LoginRequest;
import com.lennartmoeller.finance.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

class AuthControllerTest {
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private AuthController controller;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        jwtService = mock(JwtService.class);
        controller = new AuthController(authenticationManager, jwtService);
    }

    @Test
    void testLogin() {
        LoginRequest req = new LoginRequest();
        req.setUsername("user");
        req.setPassword("pass");
        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(auth.getName()).thenReturn("user");
        when(jwtService.generateToken("user")).thenReturn("token");

        ResponseEntity<AuthResponse> response = controller.login(req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("token", response.getBody().getToken());
    }
}
