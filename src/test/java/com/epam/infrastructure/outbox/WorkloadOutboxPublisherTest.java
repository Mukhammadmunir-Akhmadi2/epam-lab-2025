package com.epam.infrastructure.outbox;

import com.epam.infrastructure.dtos.TrainerWorkloadRequestDto;
import com.epam.infrastructure.integration.KafkaHeadersProvider;
import com.epam.infrastructure.outbox.entity.WorkloadOutboxEvent;
import com.epam.infrastructure.outbox.util.OutboxSerializer;
import com.epam.infrastructure.repository.JpaWorkloadOutboxRepository;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class WorkloadOutboxPublisherTest {

    private final JpaWorkloadOutboxRepository repo = mock(JpaWorkloadOutboxRepository.class);
    private final WorkloadOutboxPort outboxPort = mock(WorkloadOutboxPort.class);
    private final OutboxSerializer serializer = mock(OutboxSerializer.class);
    private final KafkaTemplate<String, TrainerWorkloadRequestDto> kafkaTemplate = mock(KafkaTemplate.class);
    private final KafkaHeadersProvider headersProvider = mock(KafkaHeadersProvider.class);

    private final WorkloadOutboxPublisher publisher =
            new WorkloadOutboxPublisher(repo, outboxPort, serializer, kafkaTemplate, headersProvider);

    private void setTopic(String topic) {
        try {
            Field f = WorkloadOutboxPublisher.class.getDeclaredField("topic");
            f.setAccessible(true);
            f.set(publisher, topic);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void publishBatch_shouldDoNothing_whenBatchEmpty() {
        when(repo.findBatchForRetry(any(LocalDateTime.class))).thenReturn(List.of());

        publisher.publishBatch();

        verify(repo).findBatchForRetry(any(LocalDateTime.class));
        verifyNoMoreInteractions(repo);
        verifyNoInteractions(serializer, outboxPort, kafkaTemplate, headersProvider);
    }

    @Test
    void publishBatch_success_shouldSendAndMarkSent() throws Exception {
        setTopic("trainer.workload.events");

        WorkloadOutboxEvent e = new WorkloadOutboxEvent();
        e.setWoeId("id-1");
        e.setAttempts(0);
        e.setTransactionId("tx-1");
        e.setPayload(Map.of("x", 1));

        when(repo.findBatchForRetry(any(LocalDateTime.class))).thenReturn(List.of(e));

        TrainerWorkloadRequestDto dto = new TrainerWorkloadRequestDto();
        dto.setTrainerUsername("john");

        when(serializer.fromMap(e.getPayload(), TrainerWorkloadRequestDto.class)).thenReturn(dto);
        when(headersProvider.innerServerAuthorizationValue()).thenReturn("Bearer abc");

        // mock kafka result
        RecordMetadata meta = mock(RecordMetadata.class);
        when(meta.topic()).thenReturn("trainer.workload.events");
        when(meta.partition()).thenReturn(2);
        when(meta.offset()).thenReturn(42L);

        @SuppressWarnings("unchecked")
        SendResult<String, TrainerWorkloadRequestDto> sendResult = mock(SendResult.class);
        when(sendResult.getRecordMetadata()).thenReturn(meta);

        // IMPORTANT: adjust this depending on your KafkaTemplate return type
        when(kafkaTemplate.send(any(Message.class)))
                .thenReturn(CompletableFuture.completedFuture(sendResult));

        publisher.publishBatch();

        verify(repo).findBatchForRetry(any(LocalDateTime.class));
        verify(serializer).fromMap(e.getPayload(), TrainerWorkloadRequestDto.class);
        verify(headersProvider).innerServerAuthorizationValue();

        ArgumentCaptor<Message<TrainerWorkloadRequestDto>> msgCaptor = ArgumentCaptor.forClass(Message.class);
        verify(kafkaTemplate).send(msgCaptor.capture());

        Message<TrainerWorkloadRequestDto> sentMsg = msgCaptor.getValue();
        assertNotNull(sentMsg);
        assertEquals(dto, sentMsg.getPayload());

        verify(outboxPort).markSent(e);
        verify(outboxPort, never()).markFailed(any(), anyString());
    }

    @Test
    void publishBatch_failure_shouldMarkFailed_whenKafkaSendThrows() {
        setTopic("trainer.workload.events");

        WorkloadOutboxEvent e = new WorkloadOutboxEvent();
        e.setWoeId("id-2");
        e.setAttempts(3);
        e.setTransactionId("tx-2");
        e.setPayload(Map.of("x", 2));

        when(repo.findBatchForRetry(any(LocalDateTime.class))).thenReturn(List.of(e));

        TrainerWorkloadRequestDto dto = new TrainerWorkloadRequestDto();
        dto.setTrainerUsername("john");

        when(serializer.fromMap(e.getPayload(), TrainerWorkloadRequestDto.class)).thenReturn(dto);
        when(headersProvider.innerServerAuthorizationValue()).thenReturn("Bearer abc");

        CompletableFuture<SendResult<String, TrainerWorkloadRequestDto>> failed = new CompletableFuture<>();
        failed.completeExceptionally(new RuntimeException("down"));
        when(kafkaTemplate.send(any(Message.class))).thenReturn(failed);

        publisher.publishBatch();

        verify(outboxPort, never()).markSent(e);

        ArgumentCaptor<String> errorCaptor = ArgumentCaptor.forClass(String.class);
        verify(outboxPort).markFailed(eq(e), errorCaptor.capture());

        assertTrue(errorCaptor.getValue().contains("down"));
    }
}