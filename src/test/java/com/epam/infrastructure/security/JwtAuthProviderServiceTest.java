package com.epam.infrastructure.security;

import com.epam.infrastructure.security.auth.JwtAuthProviderService;
import com.epam.infrastructure.security.jwt.JwtService;
import com.epam.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JwtAuthProviderServiceTest {

    private JwtService jwtService;
    private JwtAuthProviderService authProviderService;

    private SecurityContext securityContext;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        authProviderService = new JwtAuthProviderService(jwtService);

        securityContext = mock(SecurityContext.class);
        authentication = mock(Authentication.class);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void isAuthenticated_ShouldReturnTrue_WhenUserMatches() {
        User user = new User();
        user.setUsername("john");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(user);

        boolean result = authProviderService.isAuthenticated("john");
        assertThat(result).isTrue();
    }

    @Test
    void isAuthenticated_ShouldReturnFalse_WhenUserDoesNotMatch() {
        User user = new User();
        user.setUsername("alice");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(user);

        boolean result = authProviderService.isAuthenticated("bob");
        assertThat(result).isFalse();
    }

    @Test
    void isAuthenticated_ShouldReturnFalse_WhenNoAuthentication() {
        when(securityContext.getAuthentication()).thenReturn(null);
        boolean result = authProviderService.isAuthenticated("john");
        assertThat(result).isFalse();
    }

    @Test
    void generateTokenForUser_ShouldCallJwtServiceWithClaims() {
        User user = new User();
        user.setUsername("john");
        user.setFirstName("John");
        user.setLastName("Doe");

        authProviderService.generateTokenForUser(user);

        verify(jwtService, times(1)).generateToken(eq(user), argThat(claims ->
                "john".equals(claims.get("username")) &&
                        "John Doe".equals(claims.get("fullName"))
        ));
    }

    @Test
    void getAuthenticatedUser_ShouldReturnUser_WhenAuthenticated() {
        User user = new User();
        user.setUsername("john");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(user);

        User result = authProviderService.getAuthenticatedUser();
        assertThat(result).isEqualTo(user);
    }

    @Test
    void getAuthenticatedUser_ShouldReturnNull_WhenNotAuthenticated() {
        when(securityContext.getAuthentication()).thenReturn(null);
        User result = authProviderService.getAuthenticatedUser();
        assertThat(result).isNull();
    }

    @Test
    void logout_ShouldClearSecurityContext() {
        authProviderService.logout();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
