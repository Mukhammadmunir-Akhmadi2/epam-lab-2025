package com.epam.infrastructure.repository;

import com.epam.infrastructure.enums.TrainingTypeEnum;
import com.epam.model.TrainingType;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class JpaTrainingTypeRepositoryTest {

    @Autowired
    private PlatformTransactionManager transactionManager;

    @PersistenceContext
    EntityManager em;

    @Autowired
    private JpaTrainingTypeRepository trainingTypeRepository;

    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        trainingType = new TrainingType();
        trainingType.setTrainingType(TrainingTypeEnum.CARDIO);
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
        trainingType.setTrainingType(TrainingTypeEnum.CARDIO);
        TrainingType saved = trainingTypeRepository.save(trainingType);
        assertNotNull(saved.getTrainingTypeId());
        assertEquals(TrainingTypeEnum.CARDIO, saved.getTrainingType());
    }

    @Test
    void findByType_shouldReturnTrainingType() {
        trainingType.setTrainingType(TrainingTypeEnum.CARDIO);
        TrainingType saved = trainingTypeRepository.save(trainingType);
        Optional<TrainingType> found = trainingTypeRepository.findByType(TrainingTypeEnum.CARDIO);
        assertTrue(found.isPresent());
        assertEquals(saved.getTrainingTypeId(), found.get().getTrainingTypeId());
    }

    @Test
    void save_shouldUpdateExistingTrainingType() {
        trainingType.setTrainingType(TrainingTypeEnum.CARDIO);
        TrainingType saved = trainingTypeRepository.save(trainingType);
        saved.setTrainingType(TrainingTypeEnum.STRENGTH);
        TrainingType updated = trainingTypeRepository.save(saved);
        assertEquals(saved.getTrainingTypeId(), updated.getTrainingTypeId());
        assertEquals(TrainingTypeEnum.STRENGTH, updated.getTrainingType());
    }

    @Test
    void findAll_shouldReturnAllTrainingTypes() {
        TrainingType cardio = new TrainingType();
        cardio.setTrainingType(TrainingTypeEnum.CARDIO);
        TrainingType strength = new TrainingType();
        strength.setTrainingType(TrainingTypeEnum.STRENGTH);

        trainingTypeRepository.save(cardio);
        trainingTypeRepository.save(strength);

        List<TrainingType> allTypes = trainingTypeRepository.findAll();

        assertNotNull(allTypes);
        assertEquals(2, allTypes.size());

        assertTrue(allTypes.stream().anyMatch(tt -> tt.getTrainingType() == TrainingTypeEnum.CARDIO));
        assertTrue(allTypes.stream().anyMatch(tt -> tt.getTrainingType() == TrainingTypeEnum.STRENGTH));
    }

}