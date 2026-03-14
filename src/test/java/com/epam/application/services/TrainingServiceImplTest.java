package com.epam.application.services;

import com.epam.application.exceptions.TrainerScheduleConflictException;
import com.epam.application.port.WorkloadEventPublisher;
import com.epam.application.repository.TrainingRepository;
import com.epam.application.services.impl.TrainingQueryServiceImpl;
import com.epam.application.services.impl.TrainingServiceImpl;
import com.epam.application.tx.AfterCommitExecutor;
import com.epam.model.Trainer;
import com.epam.model.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TrainingQueryServiceImpl trainingQueryService;

    @Mock
    private AfterCommitExecutor afterCommitExecutor;

    @Mock
    private WorkloadEventPublisher workloadPublisher;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private Training training;
    private Trainer trainer;

    @BeforeEach
    void setUp() {

        trainer = new Trainer();
        trainer.setUsername("trainer1");

        training = new Training();
        training.setTrainingId(UUID.randomUUID().toString());
        training.setTrainingName("Morning Cardio");
        training.setIsActive(true);
        training.setTrainer(trainer);
        training.setDate(LocalDateTime.now());
        training.setDuration(60);
    }

    @Test
    void testCreateTraining_success_publishesAfterCommit() {

        when(trainingQueryService.hasTrainerConflict(any(), any(), anyInt()))
                .thenReturn(false);

        when(trainingRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        when(trainingRepository.findById(training.getTrainingId()))
                .thenReturn(Optional.of(training));

        doAnswer(inv -> {
            Runnable r = inv.getArgument(0);
            r.run();
            return null;
        }).when(afterCommitExecutor).run(any());

        Training created = trainingService.createTraining(training);

        assertNotNull(created);
        assertEquals("Morning Cardio", created.getTrainingName());

        verify(trainingQueryService)
                .hasTrainerConflict(any(), any(), anyInt());

        verify(trainingRepository).save(training);

        verify(workloadPublisher)
                .publishTrainingAdded(training);

        verify(afterCommitExecutor)
                .run(any());
    }

    @Test
    void testCreateTraining_conflict_shouldThrowException() {

        when(trainingQueryService.hasTrainerConflict(any(), any(), anyInt()))
                .thenReturn(true);

        assertThrows(
                TrainerScheduleConflictException.class,
                () -> trainingService.createTraining(training)
        );

        verify(trainingRepository, never())
                .save(any());

        verify(workloadPublisher, never())
                .publishTrainingAdded(any());
    }

    @Test
    void testGetTrainingByIdNotFound() {

        when(trainingRepository.findById("id"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> trainingService.getTrainingById("id"));
    }

    @Test
    void testGetAllTrainingsEmpty() {

        when(trainingRepository.findAll())
                .thenReturn(List.of());

        List<Training> result = trainingService.getAllTrainings();

        assertTrue(result.isEmpty());
    }
}