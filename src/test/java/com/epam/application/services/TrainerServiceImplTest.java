package com.epam.application.services;

import com.epam.application.exceptions.ResourceNotFoundException;
import com.epam.application.generators.PasswordGenerator;
import com.epam.application.generators.UsernameGenerator;
import com.epam.application.repository.BaseUserRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;

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
    @Mock
    private BaseUserRepository baseUserRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

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
        when(passwordEncoder.encode("secret123")).thenReturn("encodedSecret123");
        when(trainerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Trainer created = trainerService.createTrainer(trainer);

        assertNotNull(created.getUsername());
        assertEquals("alice.smith", created.getUsername());
        assertEquals("secret123", created.getPassword()); // raw password returned
        assertTrue(created.isActive());

        verify(trainerRepository, times(1)).save(any());
        verify(usernameGenerator, times(1)).generateUsername(any(), any());
        verify(passwordGenerator, times(1)).generatePassword(10);
        verify(passwordEncoder, times(1)).encode("secret123");
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

        assertThrows(ResourceNotFoundException.class, () -> trainerService.updateTrainer(update));

        verify(trainerRepository, times(1)).findByUserName(missingName);
        verify(trainerRepository, never()).save(any());
    }

    @Test
    void updateTrainer_shouldKeepUsername_whenNameNotChanged() {
        Trainer updatedInfo = new Trainer();
        updatedInfo.setUserId(trainer.getUserId());
        updatedInfo.setUsername(trainer.getUsername());
        updatedInfo.setFirstName("AliceUpdated");
        updatedInfo.setLastName("SmithUpdated");
        updatedInfo.setSpecialization(trainer.getSpecialization());
        updatedInfo.setActive(trainer.isActive());

        when(trainerRepository.findByUserName(trainer.getUsername())).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Trainer updated = trainerService.updateTrainer(updatedInfo);

        assertEquals(trainer.getUsername(), updated.getUsername());
        assertEquals("AliceUpdated", updated.getFirstName());
        assertEquals("SmithUpdated", updated.getLastName());

        verify(usernameGenerator, never()).generateUsername(any(), any());
        verify(trainerRepository, times(1)).save(trainer);
    }

    @Test
    void createTrainer_shouldThrow_whenRepositoryFails() {
        when(usernameGenerator.generateUsername(any(), any())).thenReturn("alice.smith");
        when(passwordGenerator.generatePassword(anyInt())).thenReturn("secret123");
        when(trainerRepository.save(any())).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> trainerService.createTrainer(trainer));
        verify(trainerRepository, times(1)).save(any());
    }
}
