package com.epam.application.services;

import com.epam.application.repository.TrainingQueryRepository;
import com.epam.application.services.impl.TrainingQueryServiceImpl;
import com.epam.model.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingQueryServiceImplTest {

    @Mock
    private TrainingQueryRepository trainingQueryRepository;

    @InjectMocks
    private TrainingQueryServiceImpl trainingQueryService;

    private Training training1;
    private Training training2;

    @BeforeEach
    void setUp() {
        training1 = new Training();
        training1.setTrainingId(UUID.randomUUID().toString());
        training1.setTrainingName("Morning Cardio");

        training2 = new Training();
        training2.setTrainingId(UUID.randomUUID().toString());
        training2.setTrainingName("Evening Cardio");
    }

    @Test
    void getTraineeTrainings_shouldReturnTrainings() {
        when(trainingQueryRepository.findTrainingsByTraineeUsernameWithFilters(
                "trainee1", null, null, null, null
        )).thenReturn(List.of(training1, training2));

        List<Training> result = trainingQueryService.getTraineeTrainings("trainee1", null, null, null, null);

        assertEquals(2, result.size());
        assertEquals("Morning Cardio", result.get(0).getTrainingName());
        verify(trainingQueryRepository, times(1))
                .findTrainingsByTraineeUsernameWithFilters("trainee1", null, null, null, null);
    }

    @Test
    void getTrainerTrainings_shouldReturnTrainings() {
        when(trainingQueryRepository.findTrainingsByTrainerUsernameWithFilters(
                "trainer1", null, null, null
        )).thenReturn(List.of(training1, training2));

        List<Training> result = trainingQueryService.getTrainerTrainings("trainer1", null, null, null);

        assertEquals(2, result.size());
        assertEquals("Morning Cardio", result.get(0).getTrainingName());
        verify(trainingQueryRepository, times(1))
                .findTrainingsByTrainerUsernameWithFilters("trainer1", null, null, null);
    }
}