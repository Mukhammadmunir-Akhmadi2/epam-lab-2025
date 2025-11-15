package com.epam.infrastructure.repository;

import com.epam.application.repository.TraineeRepository;
import com.epam.application.repository.TrainingRepository;
import com.epam.application.repository.TrainerRepository;
import com.epam.application.repository.TrainingTypeRepository;
import com.epam.infrastructure.config.AppConfig;
import com.epam.model.Trainee;
import com.epam.model.Trainer;
import com.epam.model.Training;
import com.epam.model.TrainingType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.PropertyValueException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(AppConfig.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class JpaTrainingRepositoryTest {

    @Autowired
    @Qualifier("jpaTrainingRepository")
    private TrainingRepository trainingRepository;

    @Autowired
    @Qualifier("jpaTrainerRepository")
    private TrainerRepository trainerRepository;

    @Autowired
    @Qualifier("jpaTraineeRepository")
    private TraineeRepository traineeRepository;

    @Autowired
    @Qualifier("jpaTrainingTypeRepository")
    private TrainingTypeRepository trainingTypeRepository;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private TrainingType trainingType;
    private Trainer trainer;
    private Trainee trainee;
    private Training training;

    @BeforeEach
    void setUp() {
        TransactionTemplate tx = new TransactionTemplate(transactionManager);
        tx.execute(status -> {
            em.createQuery("DELETE FROM TrainingDao t").executeUpdate();
            em.createQuery("DELETE FROM TrainerDao t").executeUpdate();
            em.createQuery("DELETE FROM TraineeDao t").executeUpdate();
            em.createQuery("DELETE FROM TrainingTypeDao tt").executeUpdate();
            return null;
        });

        trainingType = new TrainingType();
        trainingType.setTrainingType("CROSSFIT_" + UUID.randomUUID());
        trainingType = trainingTypeRepository.save(trainingType);

        trainer = new Trainer();
        trainer.setUserName("trainer_" + UUID.randomUUID());
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setPassword("pass123");
        trainer.setActive(true);
        trainer.setSpecialization(trainingType);
        trainer = trainerRepository.save(trainer);

        trainee = new Trainee();
        trainee.setUserName("trainee_" + UUID.randomUUID());
        trainee.setFirstName("Alice");
        trainee.setLastName("Smith");
        trainee.setPassword("abc123");
        trainee.setActive(true);
        trainee = traineeRepository.save(trainee);

        training = new Training();
        training.setTrainingName("Morning Cardio");
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setTrainingType(trainingType);
        training.setDate(LocalDateTime.now().plusDays(1));
        training.setDuration(60);
    }

    @Test
    void save_shouldPersistTraining() {
        Training saved = trainingRepository.save(training);
        assertNotNull(saved.getTrainingId());
        assertEquals(training.getTrainingName(), saved.getTrainingName());
    }

    @Test
    void findById_shouldReturnTraining() {
        Training saved = trainingRepository.save(training);
        Optional<Training> found = trainingRepository.findById(saved.getTrainingId());
        assertTrue(found.isPresent());
        assertEquals(saved.getTrainingName(), found.get().getTrainingName());
    }

    @Test
    void findAll_shouldReturnAllTrainings() {
        trainingRepository.save(training);

        Training another = new Training();
        another.setTrainingName("Evening Cardio");
        another.setTrainer(trainer);
        another.setTrainee(trainee);
        another.setTrainingType(trainingType);
        another.setDate(LocalDateTime.now().plusDays(2));
        another.setDuration(45);
        trainingRepository.save(another);

        List<Training> all = trainingRepository.findAll();
        assertEquals(2, all.size());
        all.forEach(t -> assertNotNull(t.getTrainingId()));
    }

    @Test
    void save_shouldFail_whenTrainingNameIsNull() {
        training.setTrainingName(null);
        assertThrows(PropertyValueException.class, () -> trainingRepository.save(training));
    }

    @Test
    void save_shouldFail_whenTrainerIsNull() {
        training.setTrainer(null);
        assertThrows(PropertyValueException.class, () -> trainingRepository.save(training));
    }

    @Test
    void save_shouldFail_whenTraineeIsNull() {
        training.setTrainee(null);
        assertThrows(PropertyValueException.class, () -> trainingRepository.save(training));
    }

    @Test
    void save_shouldFail_whenTrainingTypeIsNull() {
        training.setTrainingType(null);
        assertThrows(PropertyValueException.class, () -> trainingRepository.save(training));
    }

    @Test
    void save_shouldUpdateExistingTraining() {
        Training saved = trainingRepository.save(training);

        saved.setTrainingName("Updated Training Name");
        saved.setDuration(90);

        Training updated = trainingRepository.save(saved);

        assertEquals(saved.getTrainingId(), updated.getTrainingId(), "ID should remain the same after update");
        assertEquals("Updated Training Name", updated.getTrainingName());
        assertEquals(90, updated.getDuration());
    }
    @Test
    void findById_shouldReturnEmpty_whenTrainingDoesNotExist() {
        Optional<Training> result = trainingRepository.findById(UUID.randomUUID().toString());
        assertTrue(result.isEmpty(), "Expected Optional.empty() for non-existing training");
    }


}