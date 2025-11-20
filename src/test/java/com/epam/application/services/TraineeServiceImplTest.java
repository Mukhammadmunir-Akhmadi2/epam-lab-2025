package com.epam.application.services;

import com.epam.application.exceptions.UserNotFoundException;
import com.epam.application.generators.PasswordGenerator;
import com.epam.application.generators.UsernameGenerator;
import com.epam.application.repository.TraineeRepository;
import com.epam.application.services.impl.TraineeServiceImpl;
import com.epam.model.Trainee;
import com.epam.model.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
        defaultTrainee = new Trainee();
        defaultTrainee.setUserId(UUID.randomUUID().toString());
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
    void testTraineePermanentDelete() {
        traineeService.deleteTrainee(defaultTrainee.getUserId());

        verify(traineeRepository, never()).save(defaultTrainee);
        verify(traineeRepository, times(1)).delete(defaultTrainee.getUserId());
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

    @Test
    void testUpdateTraineeNoUsernameChange() {
        Trainee updatedInfo = new Trainee();
        updatedInfo.setUserId(defaultTrainee.getUserId());
        updatedInfo.setFirstName(defaultTrainee.getFirstName());
        updatedInfo.setLastName(defaultTrainee.getLastName());
        updatedInfo.setAddress("789 Pine St");

        when(traineeRepository.findById(defaultTrainee.getUserId())).thenReturn(Optional.of(defaultTrainee));
        when(traineeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Trainee updated = traineeService.updateTrainee(updatedInfo);

        assertEquals(defaultTrainee.getUserName(), updated.getUserName());
        assertEquals("789 Pine St", updated.getAddress());

        verify(usernameGenerator, never()).generateUsername(any(), any());
        verify(traineeRepository, times(1)).save(defaultTrainee);
    }

    @Test
    void testUpdateTraineeTrainers() {
        when(traineeRepository.findByUserName("Alice.Smith"))
                .thenReturn(Optional.of(defaultTrainee));
        when(traineeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Trainer trainer1 = new Trainer();
        trainer1.setUserId(UUID.randomUUID().toString());

        Trainer trainer2 = new Trainer();
        trainer2.setUserId(UUID.randomUUID().toString());

        var trainers = new HashSet<>(List.of(trainer1, trainer2));

        traineeService.updateTraineeTrainers("Alice.Smith", List.copyOf(trainers));

        assertEquals(2, defaultTrainee.getTrainers().size());
        verify(traineeRepository, times(1)).save(defaultTrainee);
    }

    @Test
    void updateTrainee_shouldRegenerateUsername_whenNameChanged() {
        Trainee updatedInfo = new Trainee();
        updatedInfo.setUserId(defaultTrainee.getUserId());
        updatedInfo.setFirstName("Alice");
        updatedInfo.setLastName("Johnson"); // name changed
        updatedInfo.setAddress("456 Oak Ave");

        when(traineeRepository.findById(defaultTrainee.getUserId()))
                .thenReturn(Optional.of(defaultTrainee));
        when(usernameGenerator.generateUsername(any(), any()))
                .thenReturn("alice.johnson");
        when(traineeRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Trainee updated = traineeService.updateTrainee(updatedInfo);

        assertEquals("alice.johnson", updated.getUserName());
        assertEquals("456 Oak Ave", updated.getAddress());

        verify(usernameGenerator, times(1)).generateUsername(any(), any());
        verify(traineeRepository, times(1)).save(defaultTrainee);
    }

    @Test
    void updateTrainee_shouldNotChangeUsername_whenNameNotChanged() {
        Trainee updatedInfo = new Trainee();
        updatedInfo.setUserId(defaultTrainee.getUserId());
        updatedInfo.setFirstName(defaultTrainee.getFirstName());
        updatedInfo.setLastName(defaultTrainee.getLastName());
        updatedInfo.setAddress("789 Pine St");

        when(traineeRepository.findById(defaultTrainee.getUserId()))
                .thenReturn(Optional.of(defaultTrainee));
        when(traineeRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Trainee updated = traineeService.updateTrainee(updatedInfo);

        assertEquals(defaultTrainee.getUserName(), updated.getUserName());
        assertEquals("789 Pine St", updated.getAddress());

        verify(usernameGenerator, never()).generateUsername(any(), any());
        verify(traineeRepository, times(1)).save(defaultTrainee);
    }

    @Test
    void testUpdateTraineeChangesUsername() {
        Trainee stored = new Trainee();
        stored.setUserId(defaultTrainee.getUserId());
        stored.setFirstName(defaultTrainee.getFirstName());
        stored.setLastName(defaultTrainee.getLastName());
        stored.setAddress(defaultTrainee.getAddress());
        stored.setDateOfBirth(defaultTrainee.getDateOfBirth());
        stored.setUserName(defaultTrainee.getUserName());
        stored.setActive(defaultTrainee.isActive());

        Trainee updatedInfo = new Trainee();
        updatedInfo.setUserId(defaultTrainee.getUserId());
        updatedInfo.setFirstName("Alice");
        updatedInfo.setLastName("Johnson"); // changed
        updatedInfo.setAddress("456 Oak Ave");

        when(traineeRepository.findById(defaultTrainee.getUserId()))
                .thenReturn(Optional.of(stored));   // <- NEW instance

        when(usernameGenerator.generateUsername(any(), any()))
                .thenReturn("alice.johnson");

        when(traineeRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        Trainee updated = traineeService.updateTrainee(updatedInfo);

        assertEquals("alice.johnson", updated.getUserName());
        verify(usernameGenerator, times(1)).generateUsername(any(), any());
    }

}