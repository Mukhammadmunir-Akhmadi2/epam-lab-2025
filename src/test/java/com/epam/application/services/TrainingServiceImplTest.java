package com.epam.application.services;

import com.epam.application.exceptions.TrainingNotFoundException;
import com.epam.application.repository.TrainingRepository;
import com.epam.application.services.impl.TrainingServiceImpl;
import com.epam.model.Training;
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
import static org.mockito.Mockito.*;

class TrainingServiceImplTest {

    @Mock
    private TrainingRepository trainingRepository;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private Training training;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        training = new Training();
        training.setTrainingId(UUID.randomUUID().toString());
        training.setTrainingName("Morning Cardio");
    }

    @Test
    void testCreateTraining() {
        when(trainingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Training created = trainingService.createTraining(training);

        assertNotNull(created.getTrainingId());
        assertEquals("Morning Cardio", created.getTrainingName());
        verify(trainingRepository, times(1)).save(training);
    }

    @Test
    void testGetTrainingByIdNotFound() {
        when(trainingRepository.findById("id")).thenReturn(Optional.empty());
        assertThrows(TrainingNotFoundException.class, () -> trainingService.getTrainingById("id"));
    }

    @Test
    void testGetAllTrainingsEmpty() {
        when(trainingRepository.findAll()).thenReturn(List.of());
        List<Training> result = trainingService.getAllTrainings();
        assertTrue(result.isEmpty());
    }
}