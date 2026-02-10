package com.epam.application.services;

import com.epam.application.exceptions.ResourceNotFoundException;
import com.epam.application.provider.AuthProviderService;
import com.epam.application.provider.BruteForceProtector;
import com.epam.application.repository.BaseUserRepository;
import com.epam.application.services.impl.BaseUserAuthServiceImpl;
import com.epam.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BaseUserAuthServiceImplTest {

    @Mock
    private AuthProviderService authProviderService;
    @Mock
    private BruteForceProtector bruteForceProtector;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private BaseUserRepository baseUserRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private BaseUserAuthServiceImpl baseUserAuthService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("john");
        user.setPassword("oldPass");
        user.setIsActive(true);
    }

    @Test
    void toggleActive_shouldToggleUserActiveStatus_WhenUserExists() {
        when(baseUserRepository.findByUsername("john")).thenReturn(Optional.of(user));

        boolean result = baseUserAuthService.toggleActive("john");

        assertFalse(result);
        assertFalse(user.getIsActive());
        verify(baseUserRepository).save(user);
    }

    @Test
    void toggleActive_shouldThrowException_WhenUserNotFound() {
        when(baseUserRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> baseUserAuthService.toggleActive("unknown"));
    }

    @Test
    void changePassword_shouldChangePassword_WhenOldPasswordMatches() {
        when(baseUserRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");

        baseUserAuthService.changePassword("john", "oldPass", "newPass");

        assertEquals("encodedNewPass", user.getPassword());
        verify(baseUserRepository).save(user);
    }

    @Test
    void changePassword_shouldThrowException_WhenUserNotFound() {
        when(baseUserRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> baseUserAuthService.changePassword("unknown", "old", "new"));
    }

    @Test
    void changePassword_shouldThrowException_WhenOldPasswordIncorrect() {
        when(baseUserRepository.findByUsername("john")).thenReturn(Optional.of(user));

        assertThrows(BadCredentialsException.class,
                () -> baseUserAuthService.changePassword("john", "wrongOld", "newPass"));
    }

    @Test
    void authenticateUser_shouldAuthenticateAndReturnMessage_WhenCredentialsValid() {
        Authentication authMock = mock(Authentication.class);

        when(bruteForceProtector.isBlocked("john")).thenReturn(false);
        when(authenticationManager.authenticate(any())).thenReturn(authMock);
        when(authMock.getPrincipal()).thenReturn(user);
        when(authProviderService.generateTokenForUser(user))
                .thenReturn("User john authenticated successfully.");

        String result = baseUserAuthService.authenticateUser("john", "oldPass");

        assertEquals("User john authenticated successfully.", result);
        verify(bruteForceProtector).resetAttempts("john");
        verify(authProviderService).generateTokenForUser(user);
    }

    @Test
    void authenticateUser_shouldThrowException_WhenUserBlocked() {
        when(bruteForceProtector.isBlocked("john")).thenReturn(true);
        when(bruteForceProtector.getRemainingBlockMinutes("john")).thenReturn(5L);

        BadCredentialsException ex = assertThrows(BadCredentialsException.class,
                () -> baseUserAuthService.authenticateUser("john", "oldPass"));

        assertTrue(ex.getMessage().contains("blocked"));
    }

    @Test
    void authenticateUser_shouldThrowException_WhenPasswordIncorrect() {
        when(bruteForceProtector.isBlocked("john")).thenReturn(false);

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid"));

        BadCredentialsException ex = assertThrows(BadCredentialsException.class,
                () -> baseUserAuthService.authenticateUser("john", "wrongPass"));

        assertEquals("Invalid username or password", ex.getMessage());
        verify(bruteForceProtector).recordFailedAttempt("john");
    }

    @Test
    void authenticateUser_shouldThrowException_WhenUserNotFound() {

        when(authenticationManager.authenticate(any()))
                .thenThrow(new UsernameNotFoundException("User not found"));

        assertThrows(UsernameNotFoundException.class,
                () -> baseUserAuthService.authenticateUser("unknown", "anything"));

        verify(authenticationManager, times(1)).authenticate(any());
    }

}
