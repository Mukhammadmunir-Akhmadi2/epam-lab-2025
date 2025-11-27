package com.epam.infrastructure.mappers;

import com.epam.infrastructure.daos.TrainingDao;
import com.epam.infrastructure.dtos.TrainingDto;
import com.epam.model.Trainee;
import com.epam.model.Trainer;
import com.epam.model.Training;
import com.epam.model.TrainingType;
import com.epam.infrastructure.enums.TrainingTypeEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TrainingMapperTest {

    @Autowired
    private TrainingMapper trainingMapper;

    @Test
    void testToModel() {
        Trainee trainee = new Trainee();
        trainee.setUsername("trainee1");

        Trainer trainer = new Trainer();
        trainer.setUsername("trainer1");
        trainer.setSpecialization(new TrainingType(null, null));

        TrainingDto request = new TrainingDto();
        request.setName("Yoga");
        request.setDate("2025-11-27T17:00");
        request.setDuration(60);

        Training training = trainingMapper.toModel(trainee, trainer, new TrainingType(null, null), request);

        assertNotNull(training);
        assertEquals("Yoga", training.getTrainingName());
        assertEquals("trainee1", training.getTrainee().getUsername());
        assertEquals("trainer1", training.getTrainer().getUsername());
    }

    @Test
    void testToModelFromEntities() {
        Trainee trainee = new Trainee();
        trainee.setUsername("trainee1");

        Trainer trainer = new Trainer();
        trainer.setUsername("trainer1");
        trainer.setSpecialization(new TrainingType(null, TrainingTypeEnum.CARDIO));

        TrainingDto request = new TrainingDto();
        request.setName("Yoga");
        request.setDate("2025-11-27T17:00");
        request.setDuration(60);

        Training training = trainingMapper.toModel(trainee, trainer, new TrainingType(null, TrainingTypeEnum.CARDIO), request);

        assertNotNull(training);
        assertEquals("Yoga", training.getTrainingName());
        assertEquals("trainee1", training.getTrainee().getUsername());
        assertEquals("trainer1", training.getTrainer().getUsername());
        assertEquals(60, training.getDuration());
        assertEquals(TrainingTypeEnum.CARDIO, training.getTrainingType().getTrainingType());
    }

    @Test
    void testToTrainerTrainingDtoList() {
        Training training = new Training();
        training.setTrainingName("Yoga");
        training.setDate(LocalDateTime.now());
        training.setDuration(60);

        Trainee trainee = new Trainee();
        trainee.setUsername("trainee1");
        training.setTrainee(trainee);

        training.setTrainer(new Trainer());
        training.setTrainingType(new TrainingType(null, TrainingTypeEnum.CARDIO));

        var dtoList = trainingMapper.toTrainerTrainingDtoList(List.of(training));
        assertEquals(1, dtoList.size());
        assertEquals("Yoga", dtoList.get(0).getName());
        assertEquals("trainee1", dtoList.get(0).getTrainerUsername());
    }

    @Test
    void testToTraineeTrainingDtoList() {
        Training training = new Training();
        training.setTrainingName("Strength");
        training.setDate(LocalDateTime.now());
        training.setDuration(45);

        Trainee trainee = new Trainee();
        trainee.setUsername("traineeX");
        training.setTrainee(trainee);

        Trainer trainer = new Trainer();
        trainer.setUsername("trainerX");
        training.setTrainer(trainer);

        training.setTrainingType(new TrainingType(null, TrainingTypeEnum.STRENGTH));

        var dtoList = trainingMapper.toTraineeTrainingDtoList(List.of(training));
        assertEquals(1, dtoList.size());
        assertEquals("Strength", dtoList.get(0).getName());
        assertEquals("traineeX", dtoList.get(0).getTraineeUsername());
    }

    @Test
    void testUpdateFields() {
        TrainingDao original = new TrainingDao();
        original.setTrainingName("Old Name");
        original.setDuration(30);

        TrainingDao update = new TrainingDao();
        update.setTrainingName("New Name");
        update.setDuration(60);

        trainingMapper.updateFields(update, original);

        assertEquals("New Name", original.getTrainingName());
        assertEquals(60, original.getDuration());
    }
}
