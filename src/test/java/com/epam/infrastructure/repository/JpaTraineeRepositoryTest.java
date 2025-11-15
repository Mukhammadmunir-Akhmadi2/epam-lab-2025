package com.epam.infrastructure.repository;

import com.epam.application.repository.TraineeRepository;
import com.epam.infrastructure.config.AppConfig;

import com.epam.model.Trainee;
import org.hibernate.PropertyValueException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringJUnitConfig(AppConfig.class)
@ActiveProfiles("test")
class JpaTraineeRepositoryTest {

    @Autowired
    @Qualifier("jpaTraineeRepository")
    private TraineeRepository traineeRepository;

    private Trainee trainee;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
        trainee.setUserName("john.doe");
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        trainee.setPassword("secret123");
        trainee.setActive(true);

        traineeRepository.findAll()
                .forEach(t -> traineeRepository.delete(t.getUserId()));
    }

    @Test
    void save_shouldPersistTrainee() {
        Trainee saved = traineeRepository.save(trainee);
        assertNotNull(saved.getUserId());
        assertEquals("john.doe", saved.getUserName());
    }

    @Test
    void findById_shouldReturnTrainee() {
        Trainee saved = traineeRepository.save(trainee);
        Optional<Trainee> found = traineeRepository.findById(saved.getUserId().toString());
        assertTrue(found.isPresent());
        assertEquals("john.doe", found.get().getUserName());
    }

    @Test
    void findByUserName_shouldReturnTrainee() {
        traineeRepository.save(trainee);
        Optional<Trainee> found = traineeRepository.findByUserName("john.doe");
        assertTrue(found.isPresent());
        assertEquals("john.doe", found.get().getUserName());
    }

    @Test
    void findAll_shouldReturnAllTrainees() {
        traineeRepository.save(trainee);

        Trainee another = new Trainee();
        another.setUserName("jane.doe2");
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

    // ---- FAIL CASES ----

    @Test
    void save_shouldFail_whenUserNameIsNull() {
        trainee.setUserName(null);
        assertThrows(PropertyValueException.class, () -> traineeRepository.save(trainee));
    }

    @Test
    void save_shouldFail_whenPasswordIsNull() {
        trainee.setPassword(null);
        assertThrows(PropertyValueException.class, () -> traineeRepository.save(trainee));
    }

    @Test
    void save_shouldFail_whenFirstNameIsNull() {
        trainee.setFirstName(null);
        assertThrows(PropertyValueException.class, () -> traineeRepository.save(trainee));
    }

    @Test
    void save_shouldFail_whenLastNameIsNull() {
        trainee.setLastName(null);
        assertThrows(PropertyValueException.class, () -> traineeRepository.save(trainee));
    }

    @Test
    void save_shouldFail_whenUserNameNotUnique() {
        traineeRepository.save(trainee);

        Trainee duplicate = new Trainee();
        duplicate.setUserName("john.doe");
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

        Optional<Trainee> found = traineeRepository.findById(updated.getUserId().toString());

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