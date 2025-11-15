package com.epam.application.services;

import com.epam.application.exceptions.InvalidCredentialException;
import com.epam.application.exceptions.UserNotFoundException;
import com.epam.application.repository.TrainerRepository;
import com.epam.application.services.impl.TrainerAuthServiceImpl;
import com.epam.model.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerAuthServiceImplTest {

    @Mock
    private TrainerRepository trainerRepository;

    @InjectMocks
    private TrainerAuthServiceImpl trainerAuthService;

    private Trainer defaultTrainer;

    @BeforeEach
    void setUp() {
        defaultTrainer = new Trainer();
        defaultTrainer.setUserId(UUID.randomUUID().toString());
        defaultTrainer.setUserName("john.trainer");
        defaultTrainer.setPassword("oldPass123");
        defaultTrainer.setActive(true);
    }

    @Test
    void toggleActive_shouldDeactivateActiveTrainer() {
        when(trainerRepository.findByUserName("john.trainer"))
                .thenReturn(Optional.of(defaultTrainer));
        when(trainerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        String status = trainerAuthService.toggleActive("john.trainer");

        assertEquals("deactivated", status);
        assertFalse(defaultTrainer.isActive());
        verify(trainerRepository, times(1)).save(defaultTrainer);
    }

    @Test
    void toggleActive_shouldActivateInactiveTrainer() {
        defaultTrainer.setActive(false);

        when(trainerRepository.findByUserName("john.trainer"))
                .thenReturn(Optional.of(defaultTrainer));
        when(trainerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        String status = trainerAuthService.toggleActive("john.trainer");

        assertEquals("activated", status);
        assertTrue(defaultTrainer.isActive());
        verify(trainerRepository, times(1)).save(defaultTrainer);
    }

    @Test
    void toggleActive_trainerNotFound_shouldThrowException() {
        when(trainerRepository.findByUserName("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> trainerAuthService.toggleActive("unknown"));
    }

    @Test
    void changePassword_shouldUpdatePassword() {
        when(trainerRepository.findByUserName("john.trainer"))
                .thenReturn(Optional.of(defaultTrainer));
        when(trainerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        trainerAuthService.changePassword("john.trainer", "oldPass123", "newPass456");

        assertEquals("newPass456", defaultTrainer.getPassword());
        verify(trainerRepository, times(1)).save(defaultTrainer);
    }

    @Test
    void changePassword_wrongOldPassword_shouldThrowException() {
        when(trainerRepository.findByUserName("john.trainer"))
                .thenReturn(Optional.of(defaultTrainer));

        assertThrows(InvalidCredentialException.class,
                () -> trainerAuthService.changePassword("john.trainer", "wrongPass", "newPass456"));
        verify(trainerRepository, never()).save(any());
    }

    @Test
    void changePassword_trainerNotFound_shouldThrowException() {
        when(trainerRepository.findByUserName("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> trainerAuthService.changePassword("unknown", "pass", "newPass"));
    }
}