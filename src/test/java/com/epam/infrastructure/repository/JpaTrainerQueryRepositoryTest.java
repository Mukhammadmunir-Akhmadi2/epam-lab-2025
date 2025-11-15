package com.epam.infrastructure.repository;

import com.epam.application.repository.TraineeRepository;
import com.epam.application.repository.TrainerQueryRepository;
import com.epam.application.repository.TrainerRepository;
import com.epam.application.repository.TrainingTypeRepository;
import com.epam.infrastructure.config.AppConfig;
import com.epam.model.Trainee;
import com.epam.model.Trainer;
import com.epam.model.TrainingType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(AppConfig.class)
@ActiveProfiles("test")
@Transactional
class JpaTrainerQueryRepositoryTest {

    @Autowired
    @Qualifier("jpaTrainerQueryRepository")
    private TrainerQueryRepository trainerQueryRepository;

    @Autowired
    @Qualifier("jpaTrainerRepository")
    private TrainerRepository trainerRepository;

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;

    @Autowired
    @Qualifier("jpaTraineeRepository")
    private TraineeRepository traineeRepository;

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
        trainingType.setTrainingType("YOGA_" + UUID.randomUUID());
        trainingType = trainingTypeRepository.save(trainingType);

        trainer1 = new Trainer();
        trainer1.setUserName("trainer1_" + UUID.randomUUID());
        trainer1.setFirstName("John");
        trainer1.setLastName("Doe");
        trainer1.setPassword("pass123");
        trainer1.setActive(true);
        trainer1.setSpecialization(trainingType);
        trainer1 = trainerRepository.save(trainer1);

        trainer2 = new Trainer();
        trainer2.setUserName("trainer2_" + UUID.randomUUID());
        trainer2.setFirstName("Alice");
        trainer2.setLastName("Smith");
        trainer2.setPassword("abc123");
        trainer2.setActive(true);
        trainer2.setSpecialization(trainingType);
        trainer2 = trainerRepository.save(trainer2);

        trainee = new Trainee();
        trainee.setUserName("trainee_" + UUID.randomUUID());
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
    void findUnassignedTrainersByTraineeUsername_shouldReturnAllActiveUnassignedTrainers() {
        List<Trainer> available = trainerQueryRepository.findUnassignedTrainersByTraineeUsername(trainee.getUserName());
        assertEquals(2, available.size());
        assertTrue(available.stream().anyMatch(t -> t.getUserName().equals(trainer1.getUserName())));
        assertTrue(available.stream().anyMatch(t -> t.getUserName().equals(trainer2.getUserName())));
    }

    @Test
    void findUnassignedTrainersByTraineeUsername_shouldReturnEmpty_whenTraineeDoesNotExist() {
        List<Trainer> result = trainerQueryRepository.findUnassignedTrainersByTraineeUsername("non_existing_user");
        assertTrue(result.isEmpty());
    }

    @Test
    void findUnassignedTrainersByTraineeUsername_shouldExcludeTrainersAlreadyAssignedToTrainee() {
        trainer1.getTrainees().add(trainee);
        trainerRepository.save(trainer1);

        List<Trainer> available = trainerQueryRepository.findUnassignedTrainersByTraineeUsername(trainee.getUserName());
        assertEquals(2, available.size());
        assertEquals(trainer2.getUserName(), available.get(1).getUserName());
    }
}