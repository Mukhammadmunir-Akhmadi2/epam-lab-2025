package com.epam.infrastructure.repository;

import com.epam.infrastructure.enums.TrainingTypeEnum;
import com.epam.model.Trainer;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class JpaTrainerRepositoryTest {

    @Autowired
    private JpaTrainerRepository trainerRepository;

    @Autowired
    private JpaTrainingTypeRepository trainingTypeRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @PersistenceContext
    private EntityManager em;

    private TrainingType specialization;
    private Trainer trainer;

    @BeforeEach
    void setUp() {
        specialization = new TrainingType();
        specialization.setTrainingType(TrainingTypeEnum.CARDIO);
        specialization = trainingTypeRepository.save(specialization);

        String uid = UUID.randomUUID().toString().substring(0, 8);
        trainer = new Trainer();
        trainer.setUsername("trainer_" + uid);
        trainer.setFirstName("John");
        trainer.setLastName("Doe");
        trainer.setPassword("pass123");
        trainer.setActive(true);
        trainer.setSpecialization(specialization);
    }

    @AfterEach
    void cleanUp() {
        TransactionTemplate tx = new TransactionTemplate(transactionManager);
        tx.execute(status -> {
            em.createQuery("DELETE FROM TrainerDao t").executeUpdate();
            em.createQuery("DELETE FROM TrainingTypeDao tt").executeUpdate();
            return null;
        });
    }

    @Test
    void save_shouldPersistTrainer() {
        Trainer saved = trainerRepository.save(trainer);
        assertNotNull(saved.getUserId());
        assertEquals(trainer.getUsername(), saved.getUsername());
    }

    @Test
    void findById_shouldReturnTrainer() {
        Trainer saved = trainerRepository.save(trainer);
        Optional<Trainer> found = trainerRepository.findById(saved.getUserId());
        assertTrue(found.isPresent());
        assertEquals(saved.getUsername(), found.get().getUsername());
    }

    @Test
    void findByUserName_shouldReturnTrainer() {
        Trainer saved = trainerRepository.save(trainer);
        Optional<Trainer> found = trainerRepository.findByUserName(saved.getUsername());
        assertTrue(found.isPresent());
        assertEquals(saved.getUsername(), found.get().getUsername());
    }

    @Test
    void findAll_shouldReturnAllTrainers() {
        trainerRepository.save(trainer);

        Trainer another = new Trainer();
        another.setUsername("trainer_" + UUID.randomUUID());
        another.setFirstName("Alice");
        another.setLastName("Smith");
        another.setPassword("abc123");
        another.setActive(true);
        another.setSpecialization(trainer.getSpecialization());
        trainerRepository.save(another);

        List<Trainer> all = trainerRepository.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void save_shouldFail_whenUserNameIsNull() {
        trainer.setUsername(null);
        assertThrows(DataIntegrityViolationException.class, () -> trainerRepository.save(trainer));
    }

    @Test
    void save_shouldFail_whenPasswordIsNull() {
        trainer.setPassword(null);
        assertThrows(DataIntegrityViolationException.class, () -> trainerRepository.save(trainer));
    }

    @Test
    void save_shouldFail_whenFirstNameIsNull() {
        trainer.setFirstName(null);
        assertThrows(DataIntegrityViolationException.class, () -> trainerRepository.save(trainer));
    }

    @Test
    void save_shouldFail_whenLastNameIsNull() {
        trainer.setLastName(null);
        assertThrows(DataIntegrityViolationException.class, () -> trainerRepository.save(trainer));
    }

    @Test
    void save_shouldFail_whenUserNameNotUnique() {
        Trainer saved = trainerRepository.save(trainer);

        Trainer duplicate = new Trainer();
        duplicate.setUsername(saved.getUsername());
        duplicate.setFirstName("John2");
        duplicate.setLastName("Doe2");
        duplicate.setPassword("pass456");
        duplicate.setActive(true);
        duplicate.setSpecialization(saved.getSpecialization());

        assertThrows(DataIntegrityViolationException.class,
                () -> trainerRepository.save(duplicate));
    }

    @Test
    void findByUserName_shouldReturnEmpty_whenUserDoesNotExist() {
        Optional<Trainer> result = trainerRepository.findByUserName("non_existing_trainer");
        assertFalse(result.isPresent(), "Expected Optional.empty() when trainer does not exist");
    }

}