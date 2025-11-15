package com.epam.application.services;

import com.epam.application.exceptions.UserNotFoundException;
import com.epam.application.generators.PasswordGenerator;
import com.epam.application.generators.UsernameGenerator;
import com.epam.application.repository.TrainerRepository;
import com.epam.application.services.impl.TrainerServiceImpl;
import com.epam.model.Trainer;
import com.epam.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private UsernameGenerator usernameGenerator;
    @Mock
    private PasswordGenerator passwordGenerator;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private Trainer trainer;

    @BeforeEach
    void setUp() {

        var trainingType = new TrainingType();
            trainingType.setTrainingTypeId(UUID.randomUUID().toString());
            trainingType.setTrainingType("YOGA");

        trainer = new Trainer();
        trainer.setUserId(UUID.randomUUID().toString());
        trainer.setFirstName("Alice");
        trainer.setLastName("Smith");
        trainer.setSpecialization(trainingType);
    }

    @Test
    void testCreateTrainer() {
        when(usernameGenerator.generateUsername(any(), any())).thenReturn("alice.smith");
        when(passwordGenerator.generatePassword(anyInt())).thenReturn("secret123");
        when(trainerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Trainer created = trainerService.createTrainer(trainer);

        assertNotNull(created.getUserName());
        assertEquals("alice.smith", created.getUserName());
        assertEquals("secret123", created.getPassword());
        assertTrue(created.isActive());

        verify(trainerRepository, times(1)).save(any());
        verify(usernameGenerator, times(1)).generateUsername(any(), any());
        verify(passwordGenerator, times(1)).generatePassword(10);
    }

    @Test
    void testUpdateTrainerChangesUsername() {
        Trainer updatedInfo = new Trainer();

        var trainingType = new TrainingType();
        trainingType.setTrainingTypeId(UUID.randomUUID().toString());
        trainingType.setTrainingType("YOGA");

        updatedInfo.setUserId(trainer.getUserId());
        updatedInfo.setFirstName("Alice");
        updatedInfo.setLastName("Johnson");

        when(trainerRepository.findById(trainer.getUserId())).thenReturn(Optional.of(trainer));
        when(usernameGenerator.generateUsername(any(), any())).thenReturn("alice.johnson");
        when(trainerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Trainer updated = trainerService.updateTrainer(updatedInfo);

        assertEquals("alice.johnson", updated.getUserName());

        verify(usernameGenerator, times(1)).generateUsername(any(), any());
        verify(trainerRepository, times(1)).save(trainer);
    }

    @Test
    void testGetTrainerByIdNotFound() {
        when(trainerRepository.findById("id")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> trainerService.getTrainerById("id"));
    }

    @Test
    void testGetTrainerByUserNameNotFound() {
        when(trainerRepository.findByUserName("username")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> trainerService.getTrainerByUserName("username"));
    }

    @Test
    void updateTrainer_shouldThrow_whenTrainerNotFound() {
        String missingId = "missing-id";

        when(trainerRepository.findById(missingId)).thenReturn(Optional.empty());

        Trainer update = new Trainer();
        update.setUserId(missingId);

        assertThrows(UserNotFoundException.class,
                () -> trainerService.updateTrainer(update));

        verify(trainerRepository, times(1)).findById(missingId);
        verify(trainerRepository, never()).save(any());
    }

    @Test
    void createTrainer_shouldThrow_whenRepositoryFails() {
        when(usernameGenerator.generateUsername(any(), any())).thenReturn("alice.smith");
        when(passwordGenerator.generatePassword(anyInt())).thenReturn("secret123");

        when(trainerRepository.save(any()))
                .thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> trainerService.createTrainer(trainer));

        verify(trainerRepository, times(1)).save(any());
    }
    @Test
    void updateTrainer_shouldRegenerateUsername_whenNameChanged() {
        Trainer existing = trainer;
        existing.setUserName("alice.smith");

        Trainer updatedInfo = new Trainer();
        updatedInfo.setUserId(existing.getUserId());
        updatedInfo.setFirstName("Alice");
        updatedInfo.setLastName("Johnson"); // Name changed
        updatedInfo.setSpecialization(existing.getSpecialization());
        updatedInfo.setActive(existing.isActive());

        when(trainerRepository.findById(existing.getUserId()))
                .thenReturn(Optional.of(existing));

        // mock username generator so the lambda inside is executed
        when(usernameGenerator.generateUsername(eq(existing), any()))
                .thenAnswer(inv -> {
                    // Call the lambda: collision check
                    var lambda = inv.getArgument(1, java.util.function.Predicate.class);
                    lambda.test("alice.johnson"); // triggers trainerRepository.findByUserName()

                    return "alice.johnson";
                });

        when(trainerRepository.findByUserName("alice.johnson"))
                .thenReturn(Optional.empty());

        when(trainerRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        Trainer updated = trainerService.updateTrainer(updatedInfo);

        assertEquals("alice.johnson", updated.getUserName());

        verify(usernameGenerator, times(1)).generateUsername(eq(existing), any());
        verify(trainerRepository, times(1)).findByUserName("alice.johnson"); // important: lambda check
        verify(trainerRepository, times(1)).save(existing);
    }

    @Test
    void updateTrainer_shouldKeepUsername_whenNameNotChanged() {
        Trainer existing = trainer;
        existing.setUserName("Alice.Smith");

        Trainer updatedInfo = new Trainer();
        updatedInfo.setUserId(existing.getUserId());
        updatedInfo.setFirstName(existing.getFirstName());
        updatedInfo.setLastName(existing.getLastName());
        updatedInfo.setSpecialization(existing.getSpecialization());
        updatedInfo.setActive(existing.isActive());

        when(trainerRepository.findById(existing.getUserId()))
                .thenReturn(Optional.of(existing));

        when(trainerRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        Trainer updated = trainerService.updateTrainer(updatedInfo);

        assertEquals("Alice.Smith", updated.getUserName());
        verify(usernameGenerator, never()).generateUsername(any(), any());
        verify(trainerRepository, never()).findByUserName(anyString()); // lambda MUST NOT run
        verify(trainerRepository, times(1)).save(existing);
    }
}