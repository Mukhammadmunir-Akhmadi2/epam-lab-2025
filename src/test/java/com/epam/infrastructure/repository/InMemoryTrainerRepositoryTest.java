package com.epam.infrastructure.repository;

import com.epam.infrastructure.daos.TrainerDao;
import com.epam.infrastructure.enums.TrainingType;
import com.epam.infrastructure.mappers.TrainerMapper;
import com.epam.model.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTrainerRepositoryTest {

    private InMemoryTrainerRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        repository = new InMemoryTrainerRepository();

        Map<String, TrainerDao> initialStorage = new HashMap<>();

        Trainer trainer = new Trainer();
        trainer.setUserId(UUID.randomUUID());
        trainer.setFirstName("Alice");
        trainer.setLastName("Smith");
        trainer.setUserName("Alice" + "." + "Smith");
        trainer.setSpecialization(TrainingType.CARDIO);

        initialStorage.put(
                trainer.getUserId().toString(),
                TrainerMapper.INSTANCE.toDao(trainer)
        );

        Field storageField = InMemoryTrainerRepository.class.getDeclaredField("storage");
        storageField.setAccessible(true);
        storageField.set(repository, initialStorage);
    }

    @Test
    void testSaveAndFindById() {
        Trainer newTrainer = new Trainer();
        newTrainer.setFirstName("Bob");
        newTrainer.setLastName("Johnson");
        newTrainer.setSpecialization(TrainingType.STRENGTH);

        Trainer saved = repository.save(newTrainer);
        assertNotNull(saved.getUserId());

        Optional<Trainer> found = repository.findById(saved.getUserId().toString());
        assertTrue(found.isPresent());
        assertEquals("Bob", found.get().getFirstName());
    }

    @Test
    void testFindByUserName() {
        Trainer trainer = repository.findAll().get(0);
        String username = trainer.getUserName();
        Optional<Trainer> found = repository.findByUserName(username);
        assertTrue(found.isPresent());
        assertEquals(username, found.get().getUserName());
    }

    @Test
    void testFindAll() {
        List<Trainer> all = repository.findAll();
        assertFalse(all.isEmpty());
    }
}