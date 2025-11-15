package com.epam.application.services;

import com.epam.application.exceptions.InvalidCredentialException;
import com.epam.application.exceptions.UserNotFoundException;
import com.epam.application.repository.TraineeRepository;
import com.epam.application.services.impl.TraineeAuthServiceImpl;
import com.epam.model.Trainee;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeAuthServiceImplTest {

    @Mock
    private TraineeRepository traineeRepository;

    @InjectMocks
    private TraineeAuthServiceImpl traineeAuthService;

    @Test
    void toggleActive_shouldDeactivateActiveTrainee() {
        Trainee trainee = new Trainee();
        trainee.setUserName("alice");
        trainee.setActive(true);

        when(traineeRepository.findByUserName("alice")).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(any())).thenReturn(trainee);

        String result = traineeAuthService.toggleActive("alice");

        assertEquals("deactivated", result);
        assertFalse(trainee.isActive());

        ArgumentCaptor<Trainee> captor = ArgumentCaptor.forClass(Trainee.class);
        verify(traineeRepository).save(captor.capture());
        assertFalse(captor.getValue().isActive());
    }

    @Test
    void toggleActive_shouldActivateInactiveTrainee() {
        Trainee trainee = new Trainee();
        trainee.setUserName("alice");
        trainee.setActive(false);

        when(traineeRepository.findByUserName("alice")).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(any())).thenReturn(trainee);

        String result = traineeAuthService.toggleActive("alice");

        assertEquals("activated", result);
        assertTrue(trainee.isActive());

        verify(traineeRepository).save(trainee);
    }

    @Test
    void toggleActive_shouldThrowUserNotFound() {
        when(traineeRepository.findByUserName("unknown")).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () ->
                traineeAuthService.toggleActive("unknown"));

        assertEquals("Trainer not found with username: unknown", ex.getMessage());
    }

    @Test
    void changePassword_shouldChangePasswordSuccessfully() {
        Trainee trainee = new Trainee();
        trainee.setUserName("alice");
        trainee.setPassword("old123");

        when(traineeRepository.findByUserName("alice")).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(any())).thenReturn(trainee);

        traineeAuthService.changePassword("alice", "old123", "new456");

        assertEquals("new456", trainee.getPassword());
        verify(traineeRepository).save(trainee);
    }

    @Test
    void changePassword_shouldThrowInvalidCredential() {
        Trainee trainee = new Trainee();
        trainee.setUserName("alice");
        trainee.setPassword("old123");

        when(traineeRepository.findByUserName("alice")).thenReturn(Optional.of(trainee));

        InvalidCredentialException ex = assertThrows(InvalidCredentialException.class, () ->
                traineeAuthService.changePassword("alice", "wrongOld", "new456"));

        assertEquals("Old password is incorrect", ex.getMessage());
        verify(traineeRepository, never()).save(any());
    }

    @Test
    void changePassword_shouldThrowUserNotFound() {
        when(traineeRepository.findByUserName("unknown")).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () ->
                traineeAuthService.changePassword("unknown", "old", "new"));

        assertEquals("Trainee not found with username: unknown", ex.getMessage());
        verify(traineeRepository, never()).save(any());
    }
}