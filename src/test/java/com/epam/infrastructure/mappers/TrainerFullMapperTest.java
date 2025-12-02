package com.epam.infrastructure.mappers;

import com.epam.infrastructure.daos.TraineeDao;
import com.epam.infrastructure.daos.TrainerDao;
import com.epam.infrastructure.daos.TrainingTypeDao;
import com.epam.infrastructure.dtos.TrainerResponseDto;
import com.epam.infrastructure.enums.TrainingTypeEnum;
import com.epam.model.Trainee;
import com.epam.model.Trainer;
import com.epam.model.TrainingType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class TrainerFullMapperTest {

    @Autowired
    private TrainerFullMapper mapper;

    @Test
    void testToModel() {
        UUID trainerId = UUID.randomUUID();

        TraineeDao traineeDao = new TraineeDao();
        traineeDao.setUserId(UUID.randomUUID());
        traineeDao.setFirstName("John");
        traineeDao.setLastName("Doe");

        TrainingTypeDao typeDao = new TrainingTypeDao();
        typeDao.setTrainingType(TrainingTypeEnum.CARDIO);

        TrainerDao dao = new TrainerDao();
        dao.setUserId(trainerId);
        dao.setFirstName("Mike");
        dao.setLastName("Connor");
        dao.setUsername("mike");
        dao.setActive(true);
        dao.setSpecialization(typeDao);
        dao.setTrainees(Set.of(traineeDao));

        Trainer model = mapper.toModel(dao);

        assertThat(model).isNotNull();
        assertThat(model.getUserId()).isEqualTo(trainerId.toString());
        assertThat(model.getFirstName()).isEqualTo("Mike");

        assertThat(model.getSpecialization().getTrainingType())
                .isEqualTo(TrainingTypeEnum.CARDIO);

        assertThat(model.getTrainees()).hasSize(1);
        assertThat(model.getTrainees().iterator().next().getFirstName())
                .isEqualTo("John");
    }

    @Test
    void testToDao() {
        Trainer trainer = new Trainer();
        trainer.setUserId(UUID.randomUUID().toString());
        trainer.setFirstName("Anna");
        trainer.setLastName("Smith");

        TrainingType type = new TrainingType();
        type.setTrainingType(TrainingTypeEnum.STRENGTH);
        trainer.setSpecialization(type);

        Trainee trainee = new Trainee();
        trainee.setUserId(UUID.randomUUID().toString());
        trainee.setFirstName("Bob");
        trainee.setLastName("Johnson");

        trainer.setTrainees(Set.of(trainee));

        TrainerDao dao = mapper.toDao(trainer);

        assertThat(dao).isNotNull();
        assertThat(dao.getFirstName()).isEqualTo("Anna");
        assertThat(dao.getLastName()).isEqualTo("Smith");

        assertThat(dao.getSpecialization().getTrainingType())
                .isEqualTo(TrainingTypeEnum.STRENGTH);

        assertThat(dao.getTrainees()).hasSize(1);
        TraineeDao mappedTrainee = dao.getTrainees().iterator().next();
        assertThat(mappedTrainee.getFirstName()).isEqualTo("Bob");
    }


    @Test
    void testToTrainerResponseDto() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Sam");
        trainer.setLastName("Green");

        TrainingType type = new TrainingType();
        type.setTrainingType(TrainingTypeEnum.CARDIO);
        trainer.setSpecialization(type);

        Trainee trainee = new Trainee();
        trainee.setUserId(UUID.randomUUID().toString());
        trainee.setFirstName("Lisa");
        trainee.setLastName("Stone");

        trainer.setTrainees(Set.of(trainee));

        TrainerResponseDto dto = mapper.toTrainerResponseDto(trainer);

        assertThat(dto).isNotNull();
        assertThat(dto.getFirstName()).isEqualTo("Sam");

        assertThat(dto.getSpecialization()).isEqualTo(TrainingTypeEnum.CARDIO);

        assertThat(dto.getTrainees()).hasSize(1);
        assertThat(dto.getTrainees().get(0).getFirstName()).isEqualTo("Lisa");
    }
}
