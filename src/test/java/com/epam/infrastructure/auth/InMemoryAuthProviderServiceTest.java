package com.epam.infrastructure.auth;

import com.epam.application.provider.AuthProviderService;
import com.epam.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryAuthProviderServiceTest {

    private AuthProviderService authProviderService;

    @BeforeEach
    void setUp() {
        authProviderService = new InMemoryAuthProviderService();
    }

    @Test
    void setAuthenticatedUser_shouldStoreUserAndReturnSuccessMessage() {
        User user = new User();
        user.setUsername("john");

        String result = authProviderService.setAuthenticatedUser(user);

        assertEquals("User john authenticated successfully.", result);
        assertEquals(user, authProviderService.getAuthenticatedUser());
    }

    @Test
    void isAuthenticated_shouldReturnTrue_WhenUserMatchesAuthenticatedUser() {
        User user = new User();
        user.setUsername("john");
        authProviderService.setAuthenticatedUser(user);

        assertTrue(authProviderService.isAuthenticated("john"));
    }

    @Test
    void isAuthenticated_shouldReturnFalse_WhenNoUserAuthenticated() {
        assertFalse(authProviderService.isAuthenticated("john"));
    }

    @Test
    void isAuthenticated_shouldReturnFalse_WhenUsernamesDoNotMatch() {
        User user = new User();
        user.setUsername("john");
        authProviderService.setAuthenticatedUser(user);

        assertFalse(authProviderService.isAuthenticated("doe"));
    }

    @Test
    void logout_shouldClearAuthenticatedUser() {
        User user = new User();
        user.setUsername("john");
        authProviderService.setAuthenticatedUser(user);

        authProviderService.logout();

        assertNull(authProviderService.getAuthenticatedUser());
        assertFalse(authProviderService.isAuthenticated("john"));
    }
}