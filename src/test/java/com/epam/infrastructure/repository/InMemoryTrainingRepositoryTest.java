package com.epam.infrastructure.repository;

import com.epam.infrastructure.daos.TrainingDao;
import com.epam.infrastructure.enums.TrainingType;
import com.epam.infrastructure.mappers.TrainingMapper;
import com.epam.model.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTrainingRepositoryTest {

    private InMemoryTrainingRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        repository = new InMemoryTrainingRepository();

        Map<String, TrainingDao> initialStorage = new HashMap<>();

        Training training = new Training();
        training.setTrainingId(UUID.randomUUID().toString());
        training.setTrainingName("Morning Cardio");
        training.setTrainingType(TrainingType.CARDIO);
        training.setDate(LocalDateTime.of(2024, 10, 1, 8, 0));
        training.setDuration("60m");

        initialStorage.put(
                training.getTrainingId(),
                TrainingMapper.INSTANCE.toDao(training)
        );

        Field storageField = InMemoryTrainingRepository.class.getDeclaredField("storage");
        storageField.setAccessible(true);
        storageField.set(repository, initialStorage);
    }

    @Test
    void testSaveAndFindById() {
        Training newTraining = new Training();
        newTraining.setTrainingName("Strength Basics");
        newTraining.setTrainingType(TrainingType.STRENGTH);
        newTraining.setDate(LocalDateTime.of(2024, 10, 2, 10, 0));
        newTraining.setDuration("75m");

        Training saved = repository.save(newTraining);
        assertNotNull(saved.getTrainingId());

        Optional<Training> found = repository.findById(saved.getTrainingId());
        assertTrue(found.isPresent());
        assertEquals("Strength Basics", found.get().getTrainingName());
    }

    @Test
    void testFindAll() {
        List<Training> all = repository.findAll();
        assertFalse(all.isEmpty());
    }
}
