package com.epam.infrastructure.integration;

import com.epam.infrastructure.dtos.TrainerWorkloadRequestDto;
import com.epam.infrastructure.enums.ActionType;
import com.epam.infrastructure.mappers.TrainerWorkloadEventMapper;
import com.epam.model.Trainer;
import com.epam.model.Training;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class WorkloadEventPublisherImplTest {

    @Test
    void publishTrainingAdded_shouldMapAddAndSend() {
        WorkloadServiceClientImpl gateway = mock(WorkloadServiceClientImpl.class);
        TrainerWorkloadEventMapper mapper = mock(TrainerWorkloadEventMapper.class);

        WorkloadEventPublisherImpl publisher = new WorkloadEventPublisherImpl(gateway, mapper);

        Training training = training("t1", "trainer1");
        TrainerWorkloadRequestDto dto = new TrainerWorkloadRequestDto();

        when(mapper.toDto(training, ActionType.ADD)).thenReturn(dto);

        publisher.publishTrainingAdded(training);

        verify(mapper, times(1)).toDto(training, ActionType.ADD);
        verify(gateway, times(1)).send(dto);
        verifyNoMoreInteractions(gateway, mapper);
    }

    @Test
    void publishTrainingDeleted_shouldMapDeleteAndSend() {
        WorkloadServiceClientImpl gateway = mock(WorkloadServiceClientImpl.class);
        TrainerWorkloadEventMapper mapper = mock(TrainerWorkloadEventMapper.class);

        WorkloadEventPublisherImpl publisher = new WorkloadEventPublisherImpl(gateway, mapper);

        Training training = training("t2", "trainerX");
        TrainerWorkloadRequestDto dto = new TrainerWorkloadRequestDto();

        when(mapper.toDto(training, ActionType.DELETE)).thenReturn(dto);

        publisher.publishTrainingDeleted(training);

        verify(mapper, times(1)).toDto(training, ActionType.DELETE);
        verify(gateway, times(1)).send(dto);
        verifyNoMoreInteractions(gateway, mapper);
    }

    @Test
    void publishTrainingsDeleted_shouldSendTwice_andMapTwice() {
        WorkloadServiceClientImpl gateway = mock(WorkloadServiceClientImpl.class);
        TrainerWorkloadEventMapper mapper = mock(TrainerWorkloadEventMapper.class);

        WorkloadEventPublisherImpl publisher = new WorkloadEventPublisherImpl(gateway, mapper);

        Training t1 = training("t1", "trainer1");
        Training t2 = training("t2", "trainer1");
        List<Training> trainings = List.of(t1, t2);

        TrainerWorkloadRequestDto dto1 = new TrainerWorkloadRequestDto();
        TrainerWorkloadRequestDto dto2 = new TrainerWorkloadRequestDto();

        // return dto1 then dto2, regardless of Training instance equality
        when(mapper.toDto(any(Training.class), eq(ActionType.DELETE)))
                .thenReturn(dto1, dto2);

        publisher.publishTrainingsDeleted(trainings);

        // mapper called twice
        verify(mapper, times(2)).toDto(any(Training.class), eq(ActionType.DELETE));

        // gateway called twice
        ArgumentCaptor<TrainerWorkloadRequestDto> dtoCaptor =
                ArgumentCaptor.forClass(TrainerWorkloadRequestDto.class);

        verify(gateway, times(2)).send(dtoCaptor.capture());

        List<TrainerWorkloadRequestDto> sent = dtoCaptor.getAllValues();
        assertEquals(2, sent.size());
        assertSame(dto1, sent.get(0));
        assertSame(dto2, sent.get(1));

        verifyNoMoreInteractions(gateway, mapper);
    }

    private static Training training(String id, String trainerUsername) {
        Training t = new Training();
        t.setTrainingId(id);

        Trainer trainer = new Trainer();
        trainer.setUsername(trainerUsername);

        t.setTrainer(trainer);
        return t;
    }
}
