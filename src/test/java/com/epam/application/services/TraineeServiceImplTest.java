package com.epam.application.services;

import com.epam.application.exceptions.ResourceNotFoundException;
import com.epam.application.generators.PasswordGenerator;
import com.epam.application.generators.UsernameGenerator;
import com.epam.application.port.WorkloadEventPublisher;
import com.epam.application.repository.BaseUserRepository;
import com.epam.application.repository.TraineeRepository;
import com.epam.application.services.impl.TraineeServiceImpl;
import com.epam.application.tx.AfterCommitExecutor;
import com.epam.infrastructure.repository.JpaTrainingRepository;
import com.epam.model.Trainee;
import com.epam.model.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    @Mock
    private BaseUserRepository baseUserRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private Trainee defaultTrainee;

    @Mock
    private AfterCommitExecutor afterCommitExecutor;
    @Mock
    private WorkloadEventPublisher workloadPublisher;
    @Mock
    private JpaTrainingRepository trainingRepository;


    @BeforeEach
    void setUp() {
        defaultTrainee = new Trainee();
        defaultTrainee.setUserId(UUID.randomUUID().toString());
        defaultTrainee.setFirstName("Alice");
        defaultTrainee.setLastName("Smith");
        defaultTrainee.setDateOfBirth(LocalDate.of(2000, 1, 1));
        defaultTrainee.setAddress("123 Main St");
        defaultTrainee.setUsername("Alice.Smith");
        defaultTrainee.setIsActive(true);
    }

    @Test
    void testCreateTrainee() {
        when(usernameGenerator.generateUsername(any(), any())).thenReturn("john.doe");
        when(passwordGenerator.generatePassword(anyInt())).thenReturn("secret123");
        when(passwordEncoder.encode("secret123")).thenReturn("encodedSecret123");
        when(traineeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Trainee created = traineeService.createTrainee(defaultTrainee);

        assertNotNull(created.getUsername());
        assertEquals("john.doe", created.getUsername());
        assertEquals("secret123", created.getPassword()); // raw password returned
        assertTrue(created.getIsActive());

        verify(traineeRepository, times(1)).save(any());
        verify(usernameGenerator, times(1)).generateUsername(any(), any());
        verify(passwordGenerator, times(1)).generatePassword(10);
        verify(passwordEncoder, times(1)).encode("secret123");
    }

    @Test
    void testTraineePermanentDelete_deletesAndPublishesAfterCommit() {
        when(traineeRepository.findByUserName("Alice.Smith"))
                .thenReturn(Optional.of(defaultTrainee));

        when(trainingRepository.findByTraineeUsername("Alice.Smith"))
                .thenReturn(List.of()); // or trainings

        doAnswer(inv -> {
            inv.<Runnable>getArgument(0).run(); // run immediately
            return null;
        }).when(afterCommitExecutor).run(any(Runnable.class));

        traineeService.deleteTrainee("Alice.Smith");

        verify(traineeRepository).findByUserName("Alice.Smith");
        verify(trainingRepository).findByTraineeUsername("Alice.Smith");
        verify(traineeRepository).delete(defaultTrainee.getUserId());

        verify(afterCommitExecutor).run(any(Runnable.class));
        verify(workloadPublisher).publishTrainingsDeleted(anyList());
    }


    @Test
    void testTraineePermanentDelete_notFound_throws() {
        when(traineeRepository.findByUserName("Alice.Smith"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> traineeService.deleteTrainee("Alice.Smith"));

        verify(traineeRepository, never()).delete(anyString());
        verify(trainingRepository, never()).findByTraineeUsername(anyString());
        verify(afterCommitExecutor, never()).run(any());
        verify(workloadPublisher, never()).publishTrainingsDeleted(anyList());
    }


    @Test
    void testGetTraineeByIdNotFound() {
        when(traineeRepository.findById("id")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> traineeService.getTraineeById("id"));
    }

    @Test
    void testGetTraineeByUserNameNotFound() {
        when(traineeRepository.findByUserName("username")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> traineeService.getTraineeByUserName("username"));
    }

    @Test
    void testUpdateTraineeNoUsernameChange() {
        Trainee updatedInfo = new Trainee();
        updatedInfo.setUserId(defaultTrainee.getUserId());
        updatedInfo.setUsername(defaultTrainee.getUsername());
        updatedInfo.setFirstName(defaultTrainee.getFirstName());
        updatedInfo.setLastName(defaultTrainee.getLastName());
        updatedInfo.setAddress("789 Pine St");

        when(traineeRepository.findByUserName(defaultTrainee.getUsername())).thenReturn(Optional.of(defaultTrainee));
        when(traineeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Trainee updated = traineeService.updateTrainee(updatedInfo);

        assertEquals(defaultTrainee.getUsername(), updated.getUsername());
        assertEquals("789 Pine St", updated.getAddress());

        verify(usernameGenerator, never()).generateUsername(any(), any());
        verify(traineeRepository, times(1)).save(defaultTrainee);
    }

    @Test
    void testUpdateTraineeTrainers() {
        when(traineeRepository.findByUserName("Alice.Smith")).thenReturn(Optional.of(defaultTrainee));
        when(traineeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Trainer trainer1 = new Trainer();
        trainer1.setUserId(UUID.randomUUID().toString());

        Trainer trainer2 = new Trainer();
        trainer2.setUserId(UUID.randomUUID().toString());

        var trainers = List.of(trainer1, trainer2);

        List<Trainer> updatedTrainers = traineeService.updateTraineeTrainers("Alice.Smith", trainers);

        assertEquals(2, updatedTrainers.size());
        assertTrue(defaultTrainee.getTrainers().containsAll(trainers));
        verify(traineeRepository, times(1)).save(defaultTrainee);
    }
}
