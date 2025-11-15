package com.epam.infrastructure.repository;

import com.epam.application.repository.TrainingTypeRepository;
import com.epam.infrastructure.config.AppConfig;
import com.epam.model.TrainingType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(AppConfig.class)
@ActiveProfiles("test")
class JpaTrainingTypeRepositoryTest {

    @Autowired
    private PlatformTransactionManager transactionManager;

    @PersistenceContext
    EntityManager em;

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;

    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        trainingType = new TrainingType();
        trainingType.setTrainingType("CARDIO");
    }

    @AfterEach
    void cleanUp() {
        TransactionTemplate tx = new TransactionTemplate(transactionManager);
        tx.execute(status -> {
            em.createQuery("DELETE FROM TrainingTypeDao").executeUpdate();
            return null;
        });
    }

    @Test
    void save_shouldPersistTrainingType() {
        trainingType.setTrainingType("CARDIO");
        TrainingType saved = trainingTypeRepository.save(trainingType);
        assertNotNull(saved.getTrainingTypeId());
        assertEquals("CARDIO", saved.getTrainingType());
    }

    @Test
    void findByType_shouldReturnTrainingType() {
        trainingType.setTrainingType("CARDIO");
        TrainingType saved = trainingTypeRepository.save(trainingType);
        Optional<TrainingType> found = trainingTypeRepository.findByType("CARDIO");
        assertTrue(found.isPresent());
        assertEquals(saved.getTrainingTypeId(), found.get().getTrainingTypeId());
    }

    @Test
    void findByType_shouldReturnEmptyForUnknownType() {
        Optional<TrainingType> found = trainingTypeRepository.findByType("UNKNOWN");
        assertTrue(found.isEmpty());
    }

    @Test
    void save_shouldUpdateExistingTrainingType() {
        trainingType.setTrainingType("CARDIO");
        TrainingType saved = trainingTypeRepository.save(trainingType);
        saved.setTrainingType("STRENGTH");
        TrainingType updated = trainingTypeRepository.save(saved);
        assertEquals(saved.getTrainingTypeId(), updated.getTrainingTypeId());
        assertEquals("STRENGTH", updated.getTrainingType());
    }
}