package com.epam.infrastructure.mappers;

import com.epam.infrastructure.daos.TraineeDao;
import com.epam.infrastructure.daos.TrainerDao;
import com.epam.infrastructure.daos.TrainingTypeDao;
import com.epam.infrastructure.dtos.TraineeResponseDto;
import com.epam.infrastructure.enums.TrainingTypeEnum;
import com.epam.model.Trainee;
import com.epam.model.Trainer;
import com.epam.model.TrainingType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("dev")
@SpringBootTest
class TraineeFullMapperTest {

    @Autowired
    private TraineeFullMapper mapper;

    @Test
    void testToModel() {
        UUID id = UUID.randomUUID();

        TrainerDao trainerDao = new TrainerDao();
        trainerDao.setUserId(UUID.randomUUID());
        trainerDao.setFirstName("Mike");
        trainerDao.setLastName("Connor");
        TrainingTypeDao type = new TrainingTypeDao();
        type.setTrainingType(TrainingTypeEnum.CARDIO);
        trainerDao.setSpecialization(type);

        TraineeDao dao = new TraineeDao();
        dao.setUserId(id);
        dao.setFirstName("John");
        dao.setLastName("Doe");
        dao.setUsername("john");
        dao.setPassword("123");
        dao.setActive(true);
        dao.setDateOfBirth(LocalDate.of(2000, 1, 1));
        dao.setTrainers(Set.of(trainerDao));

        Trainee model = mapper.toModel(dao);

        assertThat(model).isNotNull();
        assertThat(model.getUserId()).isEqualTo(id.toString());
        assertThat(model.getFirstName()).isEqualTo("John");
        assertThat(model.getTrainers()).hasSize(1);
        assertThat(model.getTrainers().iterator().next().getSpecialization().getTrainingType())
                .isEqualTo(TrainingTypeEnum.CARDIO);
    }

    @Test
    void testToDao() {
        Trainee trainee = new Trainee();
        trainee.setUserId(UUID.randomUUID().toString());
        trainee.setFirstName("Anna");
        trainee.setLastName("Smith");

        Trainer trainer = new Trainer();
        trainer.setUserId(UUID.randomUUID().toString());
        trainer.setFirstName("Bob");
        trainer.setLastName("Strong");
        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingType(TrainingTypeEnum.STRENGTH);
        trainer.setSpecialization(trainingType);

        trainee.setTrainers(Set.of(trainer));

        TraineeDao dao = mapper.toDao(trainee);

        assertThat(dao).isNotNull();
        assertThat(dao.getFirstName()).isEqualTo("Anna");
        assertThat(dao.getLastName()).isEqualTo("Smith");
        assertThat(dao.getTrainers()).hasSize(1);

        TrainerDao mappedTrainer = dao.getTrainers().iterator().next();
        assertThat(mappedTrainer.getSpecialization().getTrainingType()).isEqualTo(TrainingTypeEnum.STRENGTH);
    }

    @Test
    void testToTraineeResponseDto() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("Sam");
        trainee.setLastName("Green");
        trainee.setDateOfBirth(LocalDate.of(1999, 5, 10));

        Trainer trainer = new Trainer();
        trainer.setUserId(UUID.randomUUID().toString());
        trainer.setFirstName("Mike");
        trainer.setLastName("Connor");
        TrainingType type = new TrainingType();
        type.setTrainingType(TrainingTypeEnum.CARDIO);
        trainer.setSpecialization(type);

        trainee.setTrainers(Set.of(trainer));

        TraineeResponseDto dto = mapper.toTraineeResponseDto(trainee);

        assertThat(dto).isNotNull();
        assertThat(dto.getFirstName()).isEqualTo("Sam");
        assertThat(dto.getDateOfBirth()).isEqualTo("1999-05-10");
        assertThat(dto.getTrainers()).hasSize(1);
        assertThat(dto.getTrainers().get(0).getSpecialization().toString()).isEqualTo("CARDIO");
    }
}
