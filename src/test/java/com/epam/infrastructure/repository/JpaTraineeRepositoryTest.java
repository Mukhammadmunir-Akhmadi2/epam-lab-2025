package com.epam.infrastructure.repository;

import com.epam.model.Trainee;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ActiveProfiles("dev")
@SpringBootTest
class JpaTraineeRepositoryTest {

    @Autowired
    private JpaTraineeRepository traineeRepository;

    private Trainee trainee;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @PersistenceContext
    private EntityManager em;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
        trainee.setUsername("john.doe");
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        trainee.setPassword("secret123");
        trainee.setActive(true);
    }

    @AfterEach
    void cleanUp() {
        TransactionTemplate tx = new TransactionTemplate(transactionManager);
        tx.execute(status -> {
            em.createQuery("DELETE FROM TraineeDao t").executeUpdate();
            em.createQuery("DELETE FROM TrainingTypeDao tt").executeUpdate();
            return null;
        });
    }

    @Test
    void save_shouldPersistTrainee() {
        Trainee saved = traineeRepository.save(trainee);
        assertNotNull(saved.getUserId());
        assertEquals("john.doe", saved.getUsername());
    }

    @Test
    void findById_shouldReturnTrainee() {
        Trainee saved = traineeRepository.save(trainee);
        Optional<Trainee> found = traineeRepository.findById(saved.getUserId());
        assertTrue(found.isPresent());
        assertEquals("john.doe", found.get().getUsername());
    }

    @Test
    void findByUserName_shouldReturnTrainee() {
        traineeRepository.save(trainee);
        Optional<Trainee> found = traineeRepository.findByUserName("john.doe");
        assertTrue(found.isPresent());
        assertEquals("john.doe", found.get().getUsername());
    }

    @Test
    void findAll_shouldReturnAllTrainees() {
        traineeRepository.save(trainee);

        Trainee another = new Trainee();
        another.setUsername("jane.doe2");
        another.setFirstName("Jane");
        another.setLastName("Doe");
        another.setPassword("pass1234");
        another.setActive(true);
        traineeRepository.save(another);

        List<Trainee> all = traineeRepository.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void delete_shouldRemoveTrainee() {
        Trainee saved = traineeRepository.save(trainee);
        traineeRepository.delete(saved.getUserId());
        Optional<Trainee> found = traineeRepository.findById(saved.getUserId());
        assertFalse(found.isPresent());
    }


    @Test
    void save_shouldFail_whenUserNameIsNull() {
        trainee.setUsername(null);
        assertThrows(DataIntegrityViolationException.class, () -> traineeRepository.save(trainee));
    }

    @Test
    void save_shouldFail_whenPasswordIsNull() {
        trainee.setPassword(null);
        assertThrows(DataIntegrityViolationException.class, () -> traineeRepository.save(trainee));
    }

    @Test
    void save_shouldFail_whenFirstNameIsNull() {
        trainee.setFirstName(null);
        assertThrows(DataIntegrityViolationException.class, () -> traineeRepository.save(trainee));
    }

    @Test
    void save_shouldFail_whenLastNameIsNull() {
        trainee.setLastName(null);
        assertThrows(DataIntegrityViolationException.class, () -> traineeRepository.save(trainee));
    }

    @Test
    void save_shouldFail_whenUserNameNotUnique() {
        traineeRepository.save(trainee);

        Trainee duplicate = new Trainee();
        duplicate.setUsername("john.doe");
        duplicate.setFirstName("John2");
        duplicate.setLastName("Doe2");
        duplicate.setPassword("secret456");
        duplicate.setActive(true);

        assertThrows(DataIntegrityViolationException.class, () -> traineeRepository.save(duplicate));
    }

    @Test
    void save_shouldUpdateExistingTrainee() {
        Trainee saved = traineeRepository.save(trainee);

        saved.setFirstName("UpdatedJohn");
        saved.setLastName("UpdatedDoe");
        saved.setPassword("newSecret123");

        Trainee updated = traineeRepository.save(saved);

        Optional<Trainee> found = traineeRepository.findById(updated.getUserId());

        assertTrue(found.isPresent());
        assertEquals("UpdatedJohn", found.get().getFirstName());
        assertEquals("UpdatedDoe", found.get().getLastName());
        assertEquals("newSecret123", found.get().getPassword());
    }

    @Test
    void findByUserName_shouldReturnEmpty_whenUserDoesNotExist() {
        Optional<Trainee> result = traineeRepository.findByUserName("non_existing_user");
        assertFalse(result.isPresent(), "Expected Optional.empty() when user does not exist");
    }
}