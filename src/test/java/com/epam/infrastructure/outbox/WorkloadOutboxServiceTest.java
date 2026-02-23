package com.epam.infrastructure.outbox;

import com.epam.infrastructure.dtos.TrainerWorkloadRequestDto;
import com.epam.infrastructure.enums.OutboxEventType;
import com.epam.infrastructure.enums.OutboxStatus;
import com.epam.infrastructure.logging.TransactionIdFilter;
import com.epam.infrastructure.outbox.entity.WorkloadOutboxEvent;
import com.epam.infrastructure.outbox.util.OutboxSerializer;
import com.epam.infrastructure.repository.JpaWorkloadOutboxRepository;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.slf4j.MDC;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WorkloadOutboxServiceTest {

    private JpaWorkloadOutboxRepository repo;
    private OutboxSerializer serializer;
    private WorkloadOutboxService service;

    @BeforeEach
    void setUp() {
        repo = mock(JpaWorkloadOutboxRepository.class);
        serializer = mock(OutboxSerializer.class);
        service = new WorkloadOutboxService(repo, serializer);
        MDC.clear();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void enqueue_shouldCreateNewOutboxEvent_andSave_withMdcTransactionId() {
        TrainerWorkloadRequestDto req = new TrainerWorkloadRequestDto();
        String aggregateId = "trainer:john";
        String error = "initial error";
        String txId = "tx-123";

        // FIX: use same key as production code
        MDC.put(TransactionIdFilter.TRANSACTION_ID_HEADER, txId);

        when(serializer.toMap(req)).thenReturn(Map.of("ok", true));

        when(repo.save(any(WorkloadOutboxEvent.class))).thenAnswer(inv -> {
            WorkloadOutboxEvent e = inv.getArgument(0);
            e.setWoeId("11111111-1111-1111-1111-111111111111");
            return e;
        });

        service.enqueue(req, aggregateId, error);

        ArgumentCaptor<WorkloadOutboxEvent> captor = ArgumentCaptor.forClass(WorkloadOutboxEvent.class);
        verify(repo, times(1)).save(captor.capture());
        verify(serializer, times(1)).toMap(req);

        WorkloadOutboxEvent saved = captor.getValue();
        assertNotNull(saved);

        assertEquals(OutboxEventType.TRAINING_WORKLOAD, saved.getEventType());
        assertEquals(OutboxStatus.NEW, saved.getStatus());
        assertEquals(0, saved.getAttempts());
        assertEquals(aggregateId, saved.getAggregateId());
        assertEquals(txId, saved.getTransactionId());
        assertEquals(Map.of("ok", true), saved.getPayload());
        assertEquals(error, saved.getLastError());

        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getNextAttemptAt());

        long diffSeconds = Duration.between(saved.getCreatedAt(), saved.getNextAttemptAt()).getSeconds();
        assertEquals(5, diffSeconds);
    }

    @Test
    void enqueue_shouldUseNoTx_whenMdcTransactionIdMissing() {
        TrainerWorkloadRequestDto req = new TrainerWorkloadRequestDto();
        String aggregateId = "trainer:john";

        when(serializer.toJson(req)).thenReturn("{}");
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.enqueue(req, aggregateId, null);

        ArgumentCaptor<WorkloadOutboxEvent> captor = ArgumentCaptor.forClass(WorkloadOutboxEvent.class);
        verify(repo).save(captor.capture());

        assertEquals("no-tx", captor.getValue().getTransactionId());
    }

    @Test
    void markSent_shouldSetStatusSent_andSave() {
        WorkloadOutboxEvent e = new WorkloadOutboxEvent();
        e.setStatus(OutboxStatus.NEW);
        e.setAttempts(0);
        e.setAggregateId("trainer:john");

        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.markSent(e);

        assertEquals(OutboxStatus.SENT, e.getStatus());
        verify(repo, times(1)).save(e);
    }

    @Test
    void markFailed_shouldScheduleRetry_whenAttemptsBelowMax() {
        WorkloadOutboxEvent e = new WorkloadOutboxEvent();
        e.setStatus(OutboxStatus.NEW);
        e.setAttempts(0); // will become 1
        e.setAggregateId("trainer:john");
        e.setNextAttemptAt(LocalDateTime.now());

        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LocalDateTime before = LocalDateTime.now();
        service.markFailed(e, "x".repeat(1000)); // should be truncated to 900
        LocalDateTime after = LocalDateTime.now();

        assertEquals(1, e.getAttempts());
        assertEquals(OutboxStatus.RETRY, e.getStatus());
        assertNotNull(e.getNextAttemptAt());

        long secondsFromBefore = Duration.between(before, e.getNextAttemptAt()).getSeconds();
        long secondsFromAfter = Duration.between(after, e.getNextAttemptAt()).getSeconds();
        assertTrue(secondsFromBefore >= 4 && secondsFromBefore <= 6, "nextAttemptAt should be about now+5s");
        assertTrue(secondsFromAfter >= 4 && secondsFromAfter <= 6, "nextAttemptAt should be about now+5s");

        assertNotNull(e.getLastError());
        assertEquals(900, e.getLastError().length(), "error must be truncated to 900 chars");

        verify(repo, times(1)).save(e);
    }

    @Test
    void markFailed_shouldMoveToDead_whenAttemptsReachMax() {
        WorkloadOutboxEvent e = new WorkloadOutboxEvent();
        e.setStatus(OutboxStatus.RETRY);
        e.setAttempts(9); // will become 10 => DEAD
        e.setAggregateId("trainer:john");

        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LocalDateTime before = LocalDateTime.now();
        service.markFailed(e, "final error");
        LocalDateTime after = LocalDateTime.now();

        assertEquals(10, e.getAttempts());
        assertEquals(OutboxStatus.DEAD, e.getStatus());
        assertEquals("final error", e.getLastError());

        assertNotNull(e.getNextAttemptAt());
        assertTrue(e.getNextAttemptAt().isAfter(before.plusYears(9)), "nextAttemptAt should be far in the future");
        assertTrue(e.getNextAttemptAt().isAfter(after.plusYears(9)), "nextAttemptAt should be far in the future");

        verify(repo, times(1)).save(e);
    }

    @Test
    void markFailed_shouldHandleNullError_withoutTruncationNpe() {
        WorkloadOutboxEvent e = new WorkloadOutboxEvent();
        e.setAttempts(0);
        e.setAggregateId("trainer:john");

        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> service.markFailed(e, null));
        assertNull(e.getLastError());

        verify(repo).save(e);
    }
}