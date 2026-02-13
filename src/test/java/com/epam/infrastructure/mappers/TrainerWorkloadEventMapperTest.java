package com.epam.infrastructure.mappers;

import com.epam.infrastructure.dtos.TrainerWorkloadRequestDto;
import com.epam.infrastructure.enums.ActionType;
import com.epam.model.Trainer;
import com.epam.model.Training;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class TrainerWorkloadEventMapperTest {

    @Autowired
    private TrainerWorkloadEventMapper mapper;

    @Test
    void toDto_shouldMapTrainerAndTrainingFields() {
        Trainer trainer = new Trainer();
        trainer.setUsername("john");
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setIsActive(true);

        Training training = new Training();
        training.setTrainer(trainer);
        training.setDate(LocalDateTime.of(2026, 2, 9, 10, 30));
        training.setDuration(45);

        TrainerWorkloadRequestDto dto = mapper.toDto(training, ActionType.ADD);

        assertNotNull(dto);
        assertEquals("john", dto.getTrainerUsername());
        assertEquals("John", dto.getTrainerFirstName());
        assertEquals("Doe", dto.getTrainerLastName());
        assertEquals(true, dto.getIsActive());
        assertEquals(training.getDate().toLocalDate(), dto.getTrainingDate());
        assertEquals(45, dto.getTrainingDuration());
        assertEquals(ActionType.ADD, dto.getActionType());
    }

    @Test
    void toDto_shouldSupportDeleteAction() {
        Trainer trainer = new Trainer();
        trainer.setUsername("trainer1");

        Training training = new Training();
        training.setTrainer(trainer);
        training.setDate(LocalDateTime.of(2026, 2, 9, 0, 0));
        training.setDuration(30);

        TrainerWorkloadRequestDto dto = mapper.toDto(training, ActionType.DELETE);

        assertNotNull(dto);
        assertEquals(ActionType.DELETE, dto.getActionType());
    }
}
