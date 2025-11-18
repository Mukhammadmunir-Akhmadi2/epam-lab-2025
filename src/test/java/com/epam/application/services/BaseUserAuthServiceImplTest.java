package com.epam.application.services;

import com.epam.application.exceptions.InvalidCredentialException;
import com.epam.application.exceptions.UserNotFoundException;
import com.epam.application.provider.AuthProviderService;
import com.epam.application.repository.BaseUserRepository;
import com.epam.application.services.impl.BaseUserAuthServiceImpl;
import com.epam.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BaseUserAuthServiceImplTest {

    @Mock
    private AuthProviderService authProviderService;

    @Mock
    private BaseUserRepository baseUserRepository;

    @InjectMocks
    private BaseUserAuthServiceImpl baseUserAuthService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserName("john");
        user.setPassword("oldPass");
        user.setActive(true);
    }

    @Test
    void toggleActive_shouldToggleUserActiveStatus_WhenUserExists() {
        when(baseUserRepository.findByUserName("john")).thenReturn(Optional.of(user));

        boolean result = baseUserAuthService.toggleActive("john");

        assertFalse(result);  // was true → now false
        assertFalse(user.isActive());
        verify(baseUserRepository).save(user);
    }

    @Test
    void toggleActive_shouldThrowException_WhenUserNotFound() {
        when(baseUserRepository.findByUserName("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> baseUserAuthService.toggleActive("unknown"));
    }

    @Test
    void changePassword_shouldChangePassword_WhenOldPasswordMatches() {
        when(baseUserRepository.findByUserName("john")).thenReturn(Optional.of(user));

        baseUserAuthService.changePassword("john", "oldPass", "newPass");

        assertEquals("newPass", user.getPassword());
        verify(baseUserRepository).save(user);
    }

    @Test
    void changePassword_shouldThrowException_WhenUserNotFound() {
        when(baseUserRepository.findByUserName("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> baseUserAuthService.changePassword("unknown", "old", "new"));
    }

    @Test
    void changePassword_shouldThrowException_WhenOldPasswordIncorrect() {
        when(baseUserRepository.findByUserName("john")).thenReturn(Optional.of(user));

        assertThrows(InvalidCredentialException.class,
                () -> baseUserAuthService.changePassword("john", "wrongOld", "newPass"));
    }

    @Test
    void authenticateUser_shouldAuthenticateAndReturnMessage_WhenCredentialsValid() {
        when(baseUserRepository.findByUserName("john")).thenReturn(Optional.of(user));
        when(authProviderService.setAuthenticatedUser(user))
                .thenReturn("User john authenticated successfully.");

        String result = baseUserAuthService.authenticateUser("john", "oldPass");

        assertEquals("User john authenticated successfully.", result);
        verify(authProviderService).setAuthenticatedUser(user);
    }

    @Test
    void authenticateUser_shouldThrowException_WhenUserNotFound() {
        when(baseUserRepository.findByUserName("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> baseUserAuthService.authenticateUser("unknown", "anything"));
    }

    @Test
    void authenticateUser_shouldThrowException_WhenPasswordIncorrect() {
        when(baseUserRepository.findByUserName("john")).thenReturn(Optional.of(user));

        assertThrows(InvalidCredentialException.class,
                () -> baseUserAuthService.authenticateUser("john", "wrongPass"));
    }
}