package com.epam.application.services;

import com.epam.application.exceptions.UserNotFoundException;
import com.epam.application.generators.PasswordGenerator;
import com.epam.application.generators.UsernameGenerator;
import com.epam.application.repository.TrainerRepository;
import com.epam.application.services.impl.TrainerServiceImpl;
import com.epam.infrastructure.enums.TrainingType;
import com.epam.model.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

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
        MockitoAnnotations.openMocks(this);

        trainer = new Trainer();
        trainer.setUserId(UUID.randomUUID());
        trainer.setFirstName("Alice");
        trainer.setLastName("Smith");
        trainer.setSpecialization(TrainingType.YOGA);
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
        updatedInfo.setUserId(trainer.getUserId());
        updatedInfo.setFirstName("Alice");
        updatedInfo.setLastName("Johnson");
        updatedInfo.setSpecialization(TrainingType.PILATES);

        when(trainerRepository.findById(trainer.getUserId().toString())).thenReturn(Optional.of(trainer));
        when(usernameGenerator.generateUsername(any(), any())).thenReturn("alice.johnson");
        when(trainerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Trainer updated = trainerService.updateTrainer(updatedInfo);

        assertEquals("alice.johnson", updated.getUserName());
        assertEquals(TrainingType.PILATES, updated.getSpecialization());

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
    void testGetAllTrainersEmpty() {
        when(trainerRepository.findAll()).thenReturn(List.of());
        List<Trainer> result = trainerService.getAllTrainers();
        assertTrue(result.isEmpty());
    }
}