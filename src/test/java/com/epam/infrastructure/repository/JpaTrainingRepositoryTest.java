package com.epam.infrastructure.repository;

import com.epam.infrastructure.enums.TrainingTypeEnum;
import com.epam.model.Trainee;
import com.epam.model.Trainer;
import com.epam.model.Training;
import com.epam.model.TrainingType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("dev")
@SpringBootTest
class JpaTrainingRepositoryTest {

    @Autowired
    private JpaTrainingRepository trainingRepository;

    @Autowired
    private JpaTrainerRepository trainerRepository;

    @Autowired
    private JpaTraineeRepository traineeRepository;

    @Autowired
    private JpaTrainingTypeRepository trainingTypeRepository;

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

        training = new Training();
        training.setTrainingName("Morning Cardio");
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setTrainingType(trainingType);
        training.setDate(LocalDateTime.now().plusDays(1));
        training.setDuration(60);
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
        assertThrows(DataIntegrityViolationException.class, () -> trainingRepository.save(training));
    }

    @Test
    void save_shouldFail_whenTrainerIsNull() {
        training.setTrainer(null);
        assertThrows(DataIntegrityViolationException.class, () -> trainingRepository.save(training));
    }

    @Test
    void save_shouldFail_whenTraineeIsNull() {
        training.setTrainee(null);
        assertThrows(DataIntegrityViolationException.class, () -> trainingRepository.save(training));
    }

    @Test
    void save_shouldFail_whenTrainingTypeIsNull() {
        training.setTrainingType(null);
        assertThrows(DataIntegrityViolationException.class, () -> trainingRepository.save(training));
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