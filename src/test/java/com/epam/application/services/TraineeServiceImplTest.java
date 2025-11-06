package com.epam.application.services;

import com.epam.application.exceptions.UserNotFoundException;
import com.epam.application.generators.PasswordGenerator;
import com.epam.application.generators.UsernameGenerator;
import com.epam.application.repository.TraineeRepository;
import com.epam.application.services.impl.TraineeServiceImpl;
import com.epam.model.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class TraineeServiceImplTest {

    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private UsernameGenerator usernameGenerator;
    @Mock
    private PasswordGenerator passwordGenerator;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private Trainee defaultTrainee;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        defaultTrainee = new Trainee();
        defaultTrainee.setUserId(UUID.randomUUID());
        defaultTrainee.setFirstName("Alice");
        defaultTrainee.setLastName("Smith");
        defaultTrainee.setDateOfBirth(LocalDate.of(2000, 1, 1));
        defaultTrainee.setAddress("123 Main St");
        defaultTrainee.setUserName("Alice.Smith");
        defaultTrainee.setActive(true);
    }


    @Test
    void testCreateTrainee() {
        when(usernameGenerator.generateUsername(any(), any())).thenReturn("john.doe");
        when(passwordGenerator.generatePassword(anyInt())).thenReturn("secret123");
        when(traineeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Trainee created = traineeService.createTrainee(defaultTrainee);

        assertNotNull(created.getUserName());
        assertEquals("john.doe", created.getUserName());
        assertEquals("secret123", created.getPassword());
        assertTrue(created.isActive());

        verify(traineeRepository, times(1)).save(any());
        verify(usernameGenerator, times(1)).generateUsername(any(), any());
        verify(passwordGenerator, times(1)).generatePassword(10);
    }

    @Test
    void testUpdateTraineeChangesUsername() {
        Trainee updatedInfo = new Trainee();
        updatedInfo.setUserId(defaultTrainee.getUserId());
        updatedInfo.setFirstName("Alice");
        updatedInfo.setLastName("Johnson"); // last name changed
        updatedInfo.setAddress("456 Oak Ave");

        when(traineeRepository.findById(defaultTrainee.getUserId().toString()))
                .thenReturn(Optional.of(defaultTrainee));
        when(usernameGenerator.generateUsername(any(), any())).thenReturn("alice.johnson");
        when(traineeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Trainee updated = traineeService.updateTrainee(updatedInfo);

        assertEquals("alice.johnson", updated.getUserName());
        assertEquals("456 Oak Ave", updated.getAddress());

        verify(usernameGenerator, times(1)).generateUsername(any(), any());
        verify(traineeRepository, times(1)).save(defaultTrainee);
    }

    @Test
    void testTraineePermanentDelete() {
        when(traineeRepository.findById(defaultTrainee.getUserId().toString()))
                .thenReturn(Optional.of(defaultTrainee));

        traineeService.deleteTrainee(defaultTrainee.getUserId().toString());

        verify(traineeRepository, never()).save(defaultTrainee);
        verify(traineeRepository, times(1)).delete(defaultTrainee.getUserId().toString());
    }

    @Test
    void testGetTraineeByIdNotFound() {
        when(traineeRepository.findById("id")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> traineeService.getTraineeById("id"));
    }

    @Test
    void testGetTraineeByUserNameNotFound() {
        when(traineeRepository.findByUserName("username")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> traineeService.getTraineeByUserName("username"));
    }
}