package com.epam.infrastructure.repository;

import com.epam.infrastructure.daos.WorkloadOutboxEventDao;
import com.epam.infrastructure.enums.OutboxEventType;
import com.epam.infrastructure.enums.OutboxStatus;
import com.epam.infrastructure.outbox.entity.WorkloadOutboxEvent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class JpaWorkloadOutboxRepositoryTest {

    @Autowired
    private JpaWorkloadOutboxRepository outboxRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @PersistenceContext
    private EntityManager em;

    private WorkloadOutboxEvent event;

    @BeforeEach
    void setUp() {
        event = validEvent(OutboxStatus.NEW, LocalDateTime.now().minusMinutes(1));
        event.setWoeId(null); // force persist
    }

    @AfterEach
    void cleanUp() {
        TransactionTemplate tx = new TransactionTemplate(transactionManager);
        tx.execute(status -> {
            em.createQuery("DELETE FROM WorkloadOutboxEventDao").executeUpdate();
            return null;
        });
    }

    private static WorkloadOutboxEvent validEvent(OutboxStatus status, LocalDateTime nextAttemptAt) {
        WorkloadOutboxEvent e = new WorkloadOutboxEvent();
        e.setWoeId(null); // by default new
        e.setStatus(status);
        e.setAttempts(0);
        e.setNextAttemptAt(nextAttemptAt);

        e.setCreatedAt(LocalDateTime.now());
        e.setAggregateId(UUID.randomUUID().toString());
        e.setTransactionId(UUID.randomUUID().toString());
        e.setEventType(OutboxEventType.TRAINING_WORKLOAD);
        e.setPayloadJson("{\"trainer\":\"john\",\"duration\":45}");

        e.setLastError("");

        return e;
    }

    @Test
    void save_shouldPersistNewEvent_whenIdIsNull() {
        WorkloadOutboxEvent saved = outboxRepository.save(event);

        assertNotNull(saved);
        assertNotNull(saved.getWoeId(), "ID must be generated");
        assertEquals(OutboxStatus.NEW, saved.getStatus());
        assertEquals(0, saved.getAttempts());
    }

    @Test
    void findById_shouldReturnEvent_whenExists() {
        WorkloadOutboxEvent saved = outboxRepository.save(event);

        Optional<WorkloadOutboxEvent> found = outboxRepository.findById(saved.getWoeId());

        assertTrue(found.isPresent());
        assertEquals(saved.getWoeId(), found.get().getWoeId());
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        Optional<WorkloadOutboxEvent> found = outboxRepository.findById(UUID.randomUUID().toString());
        assertTrue(found.isEmpty());
    }

    @Test
    void save_shouldUpdateExistingEvent_whenIdIsNotNull() {
        WorkloadOutboxEvent saved = outboxRepository.save(event);

        saved.setStatus(OutboxStatus.RETRY);
        saved.setAttempts(2);
        saved.setNextAttemptAt(LocalDateTime.now().plusMinutes(10));
        saved.setLastError("temporary failure"); // still non-null

        WorkloadOutboxEvent updated = outboxRepository.save(saved);

        assertEquals(saved.getWoeId(), updated.getWoeId());
        assertEquals(OutboxStatus.RETRY, updated.getStatus());
        assertEquals(2, updated.getAttempts());
        assertEquals("temporary failure", updated.getLastError());

        WorkloadOutboxEventDao dao =
                em.find(WorkloadOutboxEventDao.class, UUID.fromString(updated.getWoeId()));

        assertNotNull(dao);
        assertEquals(OutboxStatus.RETRY, dao.getStatus());
        assertEquals(2, dao.getAttempts());
        assertEquals("temporary failure", dao.getLastError());
    }

    @Test
    void save_shouldThrowEntityNotFound_whenUpdatingNonExistingId() {
        WorkloadOutboxEvent nonExisting = validEvent(OutboxStatus.NEW, LocalDateTime.now());
        nonExisting.setWoeId(UUID.randomUUID().toString()); // update branch

        assertThrows(EntityNotFoundException.class, () -> outboxRepository.save(nonExisting));
    }

    @Test
    void delete_shouldRemoveEvent_whenExists() {
        WorkloadOutboxEvent saved = outboxRepository.save(event);

        outboxRepository.delete(saved.getWoeId());

        Optional<WorkloadOutboxEvent> found = outboxRepository.findById(saved.getWoeId());
        assertTrue(found.isEmpty());
    }

    @Test
    void delete_shouldDoNothing_whenNotExists() {
        assertDoesNotThrow(() -> outboxRepository.delete(UUID.randomUUID().toString()));
    }

    @Test
    void findBatchForRetry_shouldReturnOnlyRetryableEvents() {
        LocalDateTime now = LocalDateTime.now();

        WorkloadOutboxEvent retry = validEvent(OutboxStatus.RETRY, now.minusMinutes(1));
        WorkloadOutboxEvent sent  = validEvent(OutboxStatus.SENT,  now.minusMinutes(1));
        WorkloadOutboxEvent dead  = validEvent(OutboxStatus.DEAD,  now.minusMinutes(1));

        outboxRepository.save(retry);
        outboxRepository.save(sent);
        outboxRepository.save(dead);

        List<WorkloadOutboxEvent> batch = outboxRepository.findBatchForRetry(now);

        assertNotNull(batch);

        assertTrue(batch.stream().allMatch(e ->
                (e.getStatus() == OutboxStatus.NEW || e.getStatus() == OutboxStatus.RETRY)
                        && (e.getNextAttemptAt() == null || !e.getNextAttemptAt().isAfter(now))
        ));
    }
}
