package com.epam.infrastructure.repository;

import com.epam.infrastructure.daos.TraineeDao;
import com.epam.infrastructure.mappers.TraineeMapper;
import com.epam.model.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTraineeRepositoryTest {

    private InMemoryTraineeRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        repository = new InMemoryTraineeRepository();

        Map<String, TraineeDao> initialStorage = new HashMap<>();

        Trainee trainee = new Trainee();
        trainee.setUserId(UUID.randomUUID().toString());
        trainee.setFirstName("Alice");
        trainee.setLastName("Smith");
        trainee.setUserName("Alice" + "." + "Smith");
        trainee.setDateOfBirth(LocalDate.of(2000, 1, 1));
        trainee.setAddress("123 Main St");

        initialStorage.put(
                trainee.getUserId().toString(),
                TraineeMapper.INSTANCE.toDao(trainee)
        );

        Field storageField = InMemoryTraineeRepository.class.getDeclaredField("storage");
        storageField.setAccessible(true);
        storageField.set(repository, initialStorage);
    }

    @Test
    void testSaveAndFindById() {
        Trainee newTrainee = new Trainee();
        newTrainee.setFirstName("Bob");
        newTrainee.setLastName("Johnson");
        newTrainee.setDateOfBirth(LocalDate.of(1995, 5, 15));
        newTrainee.setAddress("456 Oak Ave");

        Trainee saved = repository.save(newTrainee);
        assertNotNull(saved.getUserId());

        Optional<Trainee> found = repository.findById(saved.getUserId().toString());
        assertTrue(found.isPresent());
        assertEquals("Bob", found.get().getFirstName());
    }

    @Test
    void testDelete() {
        Trainee trainee = repository.findAll().get(0);
        repository.delete(trainee.getUserId().toString());
        assertFalse(repository.findById(trainee.getUserId().toString()).isPresent());
    }

    @Test
    void testFindByUserName() {
        Trainee trainee = repository.findAll().get(0);
        String username = trainee.getUserName();
        Optional<Trainee> found = repository.findByUserName(username);
        assertTrue(found.isPresent());
        assertEquals(username, found.get().getUserName());
    }

    @Test
    void testFindAll() {
        List<Trainee> all = repository.findAll();
        assertFalse(all.isEmpty());
    }
}
