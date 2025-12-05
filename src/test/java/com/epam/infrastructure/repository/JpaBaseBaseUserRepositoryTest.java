package com.epam.infrastructure.repository;

import com.epam.infrastructure.enums.TrainingTypeEnum;
import com.epam.model.Trainee;
import com.epam.model.Trainer;
import com.epam.model.TrainingType;
import com.epam.model.User;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class JpaBaseBaseUserRepositoryTest {

    @Autowired
    private JpaBaseBaseUserRepository userRepository;

    @Autowired
    private JpaTraineeRepository traineeRepository;

    @Autowired
    private JpaTrainerRepository trainerRepository;

    @Autowired
    private JpaTrainingTypeRepository trainingTypeRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @PersistenceContext
    private EntityManager em;

    private Trainee trainee;
    private Trainer trainer;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
        trainee.setUsername("john_trainee");
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        trainee.setPassword("pass123");
        trainee = traineeRepository.save(trainee);

        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingType(TrainingTypeEnum.CARDIO);

        trainingType = trainingTypeRepository.save(trainingType);

        trainer = new Trainer();
        trainer.setUsername("jane_trainer");
        trainer.setFirstName("Jane");
        trainer.setLastName("Doe");
        trainer.setPassword("pass456");
        trainer.setSpecialization(trainingType);
        trainer = trainerRepository.save(trainer);
    }

    @AfterEach void cleanUp() {
        TransactionTemplate tx = new TransactionTemplate(transactionManager);
        tx.execute(status -> {
            em.createQuery("DELETE FROM TraineeDao").executeUpdate();
            em.createQuery("DELETE FROM TrainerDao").executeUpdate();
            em.createQuery("DELETE FROM TrainingTypeDao").executeUpdate();
            return null;
        });
    }

    @Test
    void findByUserName_shouldReturnTrainee() {
        Optional<User> found = userRepository.findByUserName("john_trainee");
        assertTrue(found.isPresent());
        assertEquals("john_trainee", found.get().getUsername());
        assertEquals("John", found.get().getFirstName());
    }

    @Test
    void findByUserName_shouldReturnTrainer() {
        Optional<User> found = userRepository.findByUserName("jane_trainer");
        assertTrue(found.isPresent());
        assertEquals("jane_trainer", found.get().getUsername());
        assertEquals("Jane", found.get().getFirstName());
    }

    @Test
    void findByUserName_shouldReturnEmpty_whenNotExists() {
        Optional<User> found = userRepository.findByUserName("nonexistent_user");
        assertFalse(found.isPresent());
    }

    @Test
    void save_shouldUpdateTrainee() {
        trainee.setFirstName("Johnny");

        User updated = userRepository.save(trainee);

        assertEquals(trainee.getUserId(), updated.getUserId());
        assertEquals("Johnny", updated.getFirstName());
    }

    @Test
    void save_shouldUpdateTrainer() {
        trainer.setFirstName("Janet");

        User updated = userRepository.save(trainer);

        assertEquals(trainer.getUserId(), updated.getUserId());
        assertEquals("Janet", updated.getFirstName());
    }

    @Test
    void save_shouldThrow_whenUserIdIsNull() {
        User newUser = new User(); // no ID
        newUser.setUsername("new_user");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userRepository.save(newUser));
        assertEquals("Cannot update user without ID", ex.getMessage());
    }
}
