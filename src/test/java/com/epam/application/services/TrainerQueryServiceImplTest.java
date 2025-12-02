package com.epam.application.services;

import com.epam.application.repository.TrainerQueryRepository;
import com.epam.application.services.impl.TrainerQueryServiceImpl;
import com.epam.model.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerQueryServiceImplTest {

    @Mock
    private TrainerQueryRepository trainerQueryRepository;

    @InjectMocks
    private TrainerQueryServiceImpl trainerQueryService;

    private List<Trainer> trainers;

    @BeforeEach
    void setUp() {
        Trainer t1 = new Trainer();
        t1.setUsername("trainer1");
        Trainer t2 = new Trainer();
        t2.setUsername("trainer2");

        trainers = Arrays.asList(t1, t2);
    }

    @Test
    void getUnassignedActiveTrainers_shouldReturnList() {
        String traineeUsername = "trainee123";

        when(trainerQueryRepository.findUnassignedActiveTrainersByTraineeUsername(traineeUsername))
                .thenReturn(trainers);

        List<Trainer> result = trainerQueryService.getUnassignedActiveTrainers(traineeUsername);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(t -> t.getUsername().equals("trainer1")));
        assertTrue(result.stream().anyMatch(t -> t.getUsername().equals("trainer2")));

        verify(trainerQueryRepository, times(1))
                .findUnassignedActiveTrainersByTraineeUsername(traineeUsername);
    }

    @Test
    void getUnassignedActiveTrainers_shouldReturnEmptyListIfNone() {
        String traineeUsername = "trainee456";

        when(trainerQueryRepository.findUnassignedActiveTrainersByTraineeUsername(traineeUsername))
                .thenReturn(List.of());

        List<Trainer> result = trainerQueryService.getUnassignedActiveTrainers(traineeUsername);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(trainerQueryRepository, times(1))
                .findUnassignedActiveTrainersByTraineeUsername(traineeUsername);
    }
}