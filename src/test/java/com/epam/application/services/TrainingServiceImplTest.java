package com.epam.application.services;

import com.epam.application.port.WorkloadEventPublisher;
import com.epam.application.repository.TrainingRepository;
import com.epam.application.services.impl.TrainingServiceImpl;
import com.epam.application.tx.AfterCommitExecutor;
import com.epam.infrastructure.integration.WorkloadServiceClientImpl;
import com.epam.infrastructure.dtos.TrainerWorkloadRequestDto;
import com.epam.model.Trainer;
import com.epam.model.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronizationUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock
    private TrainingRepository trainingRepository;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private Training training;

    @Mock
    private AfterCommitExecutor afterCommitExecutor;
    @Mock
    private WorkloadEventPublisher workloadPublisher;



    @BeforeEach
    void setUp() {
        training = new Training();
        training.setTrainingId(UUID.randomUUID().toString());
        training.setTrainingName("Morning Cardio");
        training.setIsActive(true);
    }

    @Test
    void testCreateTraining_publishesAfterCommit() {
        // save returns the same object
        when(trainingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        // service does a findById after save
        when(trainingRepository.findById(training.getTrainingId())).thenReturn(Optional.of(training));

        // IMPORTANT: simulate "after commit" by running the runnable immediately
        doAnswer(inv -> {
            Runnable r = inv.getArgument(0);
            r.run();
            return null;
        }).when(afterCommitExecutor).run(any(Runnable.class));

        Training created = trainingService.createTraining(training);

        assertNotNull(created.getTrainingId());
        assertEquals("Morning Cardio", created.getTrainingName());

        verify(trainingRepository).save(training);
        verify(trainingRepository).findById(training.getTrainingId());

        // runnable executed => publisher called
        verify(workloadPublisher).publishTrainingAdded(training);
        verify(afterCommitExecutor).run(any(Runnable.class));
    }

    @Test
    void testGetTrainingByIdNotFound() {
        when(trainingRepository.findById("id")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> trainingService.getTrainingById("id"));
    }

    @Test
    void testGetAllTrainingsEmpty() {
        when(trainingRepository.findAll()).thenReturn(List.of());
        List<Training> result = trainingService.getAllTrainings();
        assertTrue(result.isEmpty());
    }
}