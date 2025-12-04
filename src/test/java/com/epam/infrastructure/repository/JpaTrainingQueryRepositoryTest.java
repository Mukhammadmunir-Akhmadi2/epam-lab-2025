package com.epam.infrastructure.repository;

import com.epam.infrastructure.enums.TrainingTypeEnum;
import com.epam.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("dev")
@SpringBootTest
class JpaTrainingQueryRepositoryTest {

    @Autowired
    private JpaTrainingQueryRepository trainingQueryRepository;

    @Autowired
    private JpaTrainingRepository trainingRepository;

    @Autowired
    private JpaTrainingTypeRepository trainingTypeRepository;

    @Autowired
    private JpaTrainerRepository trainerRepository;

    @Autowired
    private JpaTraineeRepository traineeRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @PersistenceContext
    private EntityManager em;

    private TrainingType trainingType;
    private Trainer trainer;
    private Trainee trainee;

    private Training training1;
    private Training training2;

    @BeforeEach
    void setUp() {
        trainingType = new TrainingType();
        trainingType.setTrainingType(TrainingTypeEnum.CARDIO);
        trainingType = trainingTypeRepository.save(trainingType);

        trainer = new Trainer();
        trainer.setUsername("trainer_" + UUID.randomUUID());
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setPassword("pass123");
        trainer.setActive(true);
        trainer.setSpecialization(trainingType);
        trainer = trainerRepository.save(trainer);

        trainee = new Trainee();
        trainee.setUsername("trainee_" + UUID.randomUUID());
        trainee.setFirstName("Alice");
        trainee.setLastName("Smith");
        trainee.setPassword("abc123");
        trainee.setActive(true);
        trainee = traineeRepository.save(trainee);

        training1 = new Training();
        training1.setTrainingName("Morning Cardio");
        training1.setTrainer(trainer);
        training1.setTrainee(trainee);
        training1.setTrainingType(trainingType);
        training1.setDate(LocalDateTime.now().plusDays(1));
        training1.setDuration(60);
        trainingRepository.save(training1);

        training2 = new Training();
        training2.setTrainingName("Evening Cardio");
        training2.setTrainer(trainer);
        training2.setTrainee(trainee);
        training2.setTrainingType(trainingType);
        training2.setDate(LocalDateTime.now().plusDays(2));
        training2.setDuration(45);
        trainingRepository.save(training2);
    }

    @AfterEach
    void cleanUp() {
        TransactionTemplate tx = new TransactionTemplate(transactionManager);
        tx.execute(status -> {
            em.createQuery("DELETE FROM TrainingDao t").executeUpdate();
            em.createQuery("DELETE FROM TrainerDao t").executeUpdate();
            em.createQuery("DELETE FROM TraineeDao t").executeUpdate();
            em.createQuery("DELETE FROM TrainingTypeDao tt").executeUpdate();
            return null;
        });
    }

    @Test
    void findTrainingsByTraineeUsernameWithFilters_shouldReturnAllForTrainee() {
        List<Training> trainings = trainingQueryRepository.findTrainingsByTraineeUsernameWithFilters(
                trainee.getUsername(), null, null, null, null
        );

        assertEquals(2, trainings.size());
        trainings.forEach(t -> assertNotNull(t.getTrainingId()));
    }

    @Test
    void findTrainingsByTraineeUsernameWithFilters_shouldFilterByTrainerName() {
        List<Training> trainings = trainingQueryRepository.findTrainingsByTraineeUsernameWithFilters(
                trainee.getUsername(), null, null, trainer.getUsername().substring(0, 6), null
        );

        assertEquals(2, trainings.size());
        trainings.forEach(t -> assertTrue(t.getTrainingName().contains("Cardio")));
    }

    @Test
    void findTrainingsByTraineeUsernameWithFilters_shouldFilterByTrainingType() {
        List<Training> trainings = trainingQueryRepository.findTrainingsByTraineeUsernameWithFilters(
                trainee.getUsername(), null, null, null, trainingType.getTrainingType()
        );

        assertEquals(2, trainings.size());
        trainings.forEach(t -> assertNotNull(t.getTrainingType().getTrainingType()));
    }

    @Test
    void findTrainingsByTrainerUsernameWithFilters_shouldReturnAllForTrainer() {
        List<Training> trainings = trainingQueryRepository.findTrainingsByTrainerUsernameWithFilters(
                trainer.getUsername(), null, null, null
        );

        assertEquals(2, trainings.size());
        trainings.forEach(t -> assertNotNull(t.getTrainingName()));
    }

    @Test
    void findTrainingsByTrainerUsernameWithFilters_shouldFilterByTraineeName() {
        List<Training> trainings = trainingQueryRepository.findTrainingsByTrainerUsernameWithFilters(
                trainer.getUsername(), null, null, trainee.getUsername()
        );

        assertEquals(2, trainings.size());
        trainings.forEach(t -> assertTrue(t.getTrainingName().contains("Cardio")));
    }

    @Test
    void findTrainingsByTraineeUsernameWithFilters_shouldFilterByDateRange() {
        LocalDate from = LocalDate.now().plusDays(1);
        LocalDate to = LocalDate.now().plusDays(1);
        List<Training> trainings = trainingQueryRepository.findTrainingsByTraineeUsernameWithFilters(
                trainee.getUsername(), from, to, null, null
        );

        assertEquals(1, trainings.size());
    }
}