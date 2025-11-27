package com.epam.application.services;

import com.epam.application.exceptions.ResourceNotFoundException;
import com.epam.application.generators.PasswordGenerator;
import com.epam.application.generators.UsernameGenerator;
import com.epam.application.repository.TrainerRepository;
import com.epam.application.services.impl.TrainerServiceImpl;
import com.epam.infrastructure.enums.TrainingTypeEnum;
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
            trainingType.setTrainingType(TrainingTypeEnum.YOGA);

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

        assertNotNull(created.getUsername());
        assertEquals("alice.smith", created.getUsername());
        assertEquals("secret123", created.getPassword());
        assertTrue(created.isActive());

        verify(trainerRepository, times(1)).save(any());
        verify(usernameGenerator, times(1)).generateUsername(any(), any());
        verify(passwordGenerator, times(1)).generatePassword(10);
    }


    @Test
    void testGetTrainerByIdNotFound() {
        when(trainerRepository.findById("id")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> trainerService.getTrainerById("id"));
    }

    @Test
    void testGetTrainerByUserNameNotFound() {
        when(trainerRepository.findByUserName("username")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> trainerService.getTrainerByUserName("username"));
    }

    @Test
    void updateTrainer_shouldThrow_whenTrainerNotFound() {
        String missingName = "name";

        when(trainerRepository.findByUserName(missingName)).thenReturn(Optional.empty());

        Trainer update = new Trainer();
        update.setUsername(missingName);

        assertThrows(ResourceNotFoundException.class,
                () -> trainerService.updateTrainer(update));

        verify(trainerRepository, times(1)).findByUserName(missingName);
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
    void updateTrainer_shouldKeepUsername_whenNameNotChanged() {
        Trainer existing = trainer;
        existing.setUsername("Alice.Smith");

        Trainer updatedInfo = new Trainer();
        updatedInfo.setUserId(existing.getUserId());
        updatedInfo.setUsername(existing.getUsername());
        updatedInfo.setFirstName(existing.getFirstName());
        updatedInfo.setLastName(existing.getLastName());
        updatedInfo.setSpecialization(existing.getSpecialization());
        updatedInfo.setActive(existing.isActive());

        when(trainerRepository.findByUserName(existing.getUsername()))
                .thenReturn(Optional.of(existing));

        when(trainerRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        Trainer updated = trainerService.updateTrainer(updatedInfo);

        assertEquals("Alice.Smith", updated.getUsername());
        verify(usernameGenerator, never()).generateUsername(any(), any());
        verify(trainerRepository, times(1)).save(existing);
    }
}