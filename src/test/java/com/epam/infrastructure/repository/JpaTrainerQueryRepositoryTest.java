package com.epam.infrastructure.repository;

import com.epam.infrastructure.enums.TrainingTypeEnum;
import com.epam.model.Trainee;
import com.epam.model.Trainer;
import com.epam.model.TrainingType;
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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class JpaTrainerQueryRepositoryTest {

    @Autowired
    private JpaTrainerQueryRepository trainerQueryRepository;

    @Autowired
    private JpaTrainerRepository trainerRepository;

    @Autowired
    private JpaTrainingTypeRepository trainingTypeRepository;

    @Autowired
    private JpaTraineeRepository traineeRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @PersistenceContext
    private EntityManager em;

    private TrainingType trainingType;
    private Trainer trainer1;
    private Trainer trainer2;
    private Trainee trainee;

    @BeforeEach
    void setUp() {
        trainingType = new TrainingType();
        trainingType.setTrainingType(TrainingTypeEnum.CARDIO);
        trainingType = trainingTypeRepository.save(trainingType);

        trainer1 = new Trainer();
        trainer1.setUsername("trainer1_" + UUID.randomUUID());
        trainer1.setFirstName("John");
        trainer1.setLastName("Doe");
        trainer1.setPassword("pass123");
        trainer1.setActive(true);
        trainer1.setSpecialization(trainingType);
        trainer1 = trainerRepository.save(trainer1);

        trainer2 = new Trainer();
        trainer2.setUsername("trainer2_" + UUID.randomUUID());
        trainer2.setFirstName("Alice");
        trainer2.setLastName("Smith");
        trainer2.setPassword("abc123");
        trainer2.setActive(true);
        trainer2.setSpecialization(trainingType);
        trainer2 = trainerRepository.save(trainer2);

        trainee = new Trainee();
        trainee.setUsername("trainee_" + UUID.randomUUID());
        trainee.setFirstName("Bob");
        trainee.setLastName("Brown");
        trainee.setPassword("123abc");
        trainee.setActive(true);
        trainee = traineeRepository.save(trainee);
    }

    @AfterEach
    void cleanUp() {
        TransactionTemplate tx = new TransactionTemplate(transactionManager);
        tx.execute(status -> {
            em.createQuery("DELETE FROM TrainerDao t").executeUpdate();
            em.createQuery("DELETE FROM TraineeDao t").executeUpdate();
            em.createQuery("DELETE FROM TrainingTypeDao tt").executeUpdate();
            return null;
        });
    }

    @Test
    void findUnassignedTrainersByTraineeUsername_shouldReturnAllActiveUnassignedActiveTrainers() {
        List<Trainer> available = trainerQueryRepository.findUnassignedActiveTrainersByTraineeUsername(trainee.getUsername());
        assertEquals(2, available.size());
        assertTrue(available.stream().anyMatch(t -> t.getUsername().equals(trainer1.getUsername())));
        assertTrue(available.stream().anyMatch(t -> t.getUsername().equals(trainer2.getUsername())));
    }

    @Test
    void findUnassignedActiveTrainersByTraineeUsername_shouldReturnEmpty_whenTraineeDoesNotExist() {
        List<Trainer> result = trainerQueryRepository.findUnassignedActiveTrainersByTraineeUsername("non_existing_user");
        assertTrue(result.isEmpty());
    }

    @Test
    void findUnassignedTrainersByTraineeUsername_shouldExcludeActiveTrainersAlreadyAssignedToTrainee() {
        trainer1.getTrainees().add(trainee);
        trainerRepository.save(trainer1);

        List<Trainer> available = trainerQueryRepository.findUnassignedActiveTrainersByTraineeUsername(trainee.getUsername());
        assertEquals(2, available.size());
        assertEquals(trainer2.getUsername(), available.get(1).getUsername());
    }
}