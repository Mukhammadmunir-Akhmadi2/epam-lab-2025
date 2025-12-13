package com.epam.infrastructure.security;

import com.epam.infrastructure.security.jwt.JwtService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    private final String secret = "VGhpcyBpcyBhIHNhbXBsZSBzZWNyZXQga2V5IGZvciBKV1Q=";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", secret);
        ReflectionTestUtils.setField(jwtService, "expirationHours", 2);

        // mock UserDetails
        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");
    }

    @Test
    void generateToken_ShouldGenerateValidToken() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertEquals("testuser", jwtService.extractUsername(token));
        assertFalse(jwtService.isTokenExpired(token));
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void generateToken_WithExtraClaims_ShouldContainClaims() {
        Map<String, Object> extraClaims = Map.of("role", "ADMIN");
        String token = jwtService.generateToken(userDetails, extraClaims);

        Claims claims = jwtService.extractAllClaims(token);
        assertEquals("ADMIN", claims.get("role"));
    }

    @Test
    void isTokenExpired_ShouldReturnTrue_ForExpiredToken() throws InterruptedException {
        ReflectionTestUtils.setField(jwtService, "expirationHours", 0);
        String token = jwtService.generateToken(userDetails);

        Thread.sleep(100);
        assertTrue(jwtService.isTokenExpired(token));
    }

    @Test
    void isTokenValid_ShouldReturnFalse_ForDifferentUser() {
        String token = jwtService.generateToken(userDetails);

        UserDetails otherUser = mock(UserDetails.class);
        when(otherUser.getUsername()).thenReturn("otheruser");

        assertFalse(jwtService.isTokenValid(token, otherUser));
    }
}
