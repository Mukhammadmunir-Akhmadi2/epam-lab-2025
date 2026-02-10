package com.epam.infrastructure.outbox;

import com.epam.infrastructure.dtos.TrainerWorkloadRequestDto;
import com.epam.infrastructure.integration.WorkloadClient;
import com.epam.infrastructure.outbox.entity.WorkloadOutboxEvent;
import com.epam.infrastructure.outbox.util.OutboxSerializer;
import com.epam.infrastructure.repository.JpaWorkloadOutboxRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.MDC;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WorkloadOutboxPublisherTest {

    private final JpaWorkloadOutboxRepository repo = mock(JpaWorkloadOutboxRepository.class);
    private final WorkloadOutboxPort outboxPort = mock(WorkloadOutboxPort.class);
    private final OutboxSerializer serializer = mock(OutboxSerializer.class);
    private final WorkloadClient workloadClient = mock(WorkloadClient.class);

    private final WorkloadOutboxPublisher publisher =
            new WorkloadOutboxPublisher(repo, outboxPort, serializer, workloadClient);

    @AfterEach
    void cleanup() {
        MDC.clear();
    }

    @Test
    void publishBatch_shouldDoNothing_whenBatchEmpty() {
        when(repo.findBatchForRetry(any(LocalDateTime.class))).thenReturn(List.of());

        publisher.publishBatch();

        verify(repo).findBatchForRetry(any(LocalDateTime.class));
        verifyNoInteractions(serializer, workloadClient, outboxPort);
        assertNull(MDC.get("transactionId"));
    }

    @Test
    void publishBatch_success_shouldSendAndMarkSent_andClearMdc() {
        WorkloadOutboxEvent e = new WorkloadOutboxEvent();
        e.setWoeId("id-1");
        e.setAttempts(0);
        e.setTransactionId("tx-1");
        e.setPayloadJson("{\"x\":1}");

        when(repo.findBatchForRetry(any(LocalDateTime.class))).thenReturn(List.of(e));

        TrainerWorkloadRequestDto dto = new TrainerWorkloadRequestDto();
        when(serializer.fromJson(e.getPayloadJson(), TrainerWorkloadRequestDto.class)).thenReturn(dto);

        publisher.publishBatch();

        verify(repo).findBatchForRetry(any(LocalDateTime.class));
        verify(serializer).fromJson(e.getPayloadJson(), TrainerWorkloadRequestDto.class);
        verify(workloadClient).send(dto);
        verify(outboxPort).markSent(e);
        verify(outboxPort, never()).markFailed(any(), anyString());

        // must be cleaned in finally
        assertNull(MDC.get("transactionId"));
    }

    @Test
    void publishBatch_failure_shouldMarkFailed_andClearMdc() {
        WorkloadOutboxEvent e = new WorkloadOutboxEvent();
        e.setWoeId("id-2");
        e.setAttempts(3);
        e.setTransactionId("tx-2");
        e.setPayloadJson("{\"x\":2}");

        when(repo.findBatchForRetry(any(LocalDateTime.class))).thenReturn(List.of(e));

        TrainerWorkloadRequestDto dto = new TrainerWorkloadRequestDto();
        when(serializer.fromJson(e.getPayloadJson(), TrainerWorkloadRequestDto.class)).thenReturn(dto);

        doThrow(new RuntimeException("down")).when(workloadClient).send(dto);

        publisher.publishBatch();

        verify(outboxPort, never()).markSent(e);

        ArgumentCaptor<String> errorCaptor = ArgumentCaptor.forClass(String.class);
        verify(outboxPort).markFailed(eq(e), errorCaptor.capture());

        assertTrue(errorCaptor.getValue().contains("down"));

        // must be cleaned in finally
        assertNull(MDC.get("transactionId"));
    }
}
