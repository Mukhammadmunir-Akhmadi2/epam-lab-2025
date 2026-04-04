package com.epam.infrastructure.integration;

import com.epam.infrastructure.dtos.TrainerWorkloadRequestDto;
import com.epam.infrastructure.outbox.WorkloadOutboxPort;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class WorkloadServiceClientImplTest {

    private static WorkloadServiceClientKafka newSvc(
            WorkloadOutboxPort outbox,
            KafkaHeadersProvider headersProvider,
            KafkaTemplate<String, TrainerWorkloadRequestDto> kafkaTemplate,
            String topic
    ) {
        WorkloadServiceClientKafka svc = new WorkloadServiceClientKafka(outbox, headersProvider, kafkaTemplate);

        // @Value won't run in pure unit tests -> set via reflection
        try {
            Field f = WorkloadServiceClientKafka.class.getDeclaredField("topic");
            f.setAccessible(true);
            f.set(svc, topic);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return svc;
    }

    @Test
    void send_success_shouldPublishToKafka_andNotEnqueueOutbox() throws Exception {
        WorkloadOutboxPort outbox = mock(WorkloadOutboxPort.class);
        KafkaHeadersProvider headersProvider = mock(KafkaHeadersProvider.class);

        @SuppressWarnings("unchecked")
        KafkaTemplate<String, TrainerWorkloadRequestDto> kafkaTemplate = mock(KafkaTemplate.class);

        when(headersProvider.currentTransactionId()).thenReturn("tx-1");
        when(headersProvider.innerServerAuthorizationValue()).thenReturn("Bearer abc");

        // mock send result (so .get(2s) returns successfully)
        RecordMetadata meta = mock(RecordMetadata.class);
        when(meta.topic()).thenReturn("trainer.workload.events");
        when(meta.partition()).thenReturn(0);
        when(meta.offset()).thenReturn(10L);

        @SuppressWarnings("unchecked")
        SendResult<String, TrainerWorkloadRequestDto> sendResult = mock(SendResult.class);
        when(sendResult.getRecordMetadata()).thenReturn(meta);

        CompletableFuture<SendResult<String, TrainerWorkloadRequestDto>> okFuture =
                CompletableFuture.completedFuture(sendResult);

        when(kafkaTemplate.send(any(Message.class))).thenReturn(okFuture);

        WorkloadServiceClientKafka svc = newSvc(outbox, headersProvider, kafkaTemplate, "trainer.workload.events");

        TrainerWorkloadRequestDto req = new TrainerWorkloadRequestDto();
        req.setTrainerUsername("john");
        req.setTrainingDate(LocalDate.of(2026, 2, 9));
        req.setTrainingDuration(45);

        svc.send(req);

        // verify kafka called
        ArgumentCaptor<Message<TrainerWorkloadRequestDto>> msgCaptor = ArgumentCaptor.forClass(Message.class);
        verify(kafkaTemplate, times(1)).send(msgCaptor.capture());

        Message<TrainerWorkloadRequestDto> sent = msgCaptor.getValue();
        assertEquals(req, sent.getPayload());

        // success -> outbox not used
        verifyNoInteractions(outbox);
    }

    @Test
    void send_failure_shouldEnqueueOutbox() throws Exception {
        WorkloadOutboxPort outbox = mock(WorkloadOutboxPort.class);
        KafkaHeadersProvider headersProvider = mock(KafkaHeadersProvider.class);

        @SuppressWarnings("unchecked")
        KafkaTemplate<String, TrainerWorkloadRequestDto> kafkaTemplate = mock(KafkaTemplate.class);

        when(headersProvider.currentTransactionId()).thenReturn("tx-1");
        when(headersProvider.innerServerAuthorizationValue()).thenReturn("Bearer abc");

        // future completes exceptionally, so .get(2s) throws ExecutionException
        CompletableFuture<SendResult<String, TrainerWorkloadRequestDto>> failed = new CompletableFuture<>();
        failed.completeExceptionally(new RuntimeException("kafka down"));
        when(kafkaTemplate.send(any(Message.class))).thenReturn(failed);

        WorkloadServiceClientKafka svc = newSvc(outbox, headersProvider, kafkaTemplate, "trainer.workload.events");

        TrainerWorkloadRequestDto req = new TrainerWorkloadRequestDto();
        req.setTrainerUsername("john");
        req.setTrainingDate(LocalDate.of(2026, 2, 9));
        req.setTrainingDuration(45);

        svc.send(req);

        verify(outbox, times(1)).enqueue(
                eq(req),
                eq("john:2026-02-09"),
                contains("kafka down")
        );
    }

    @Test
    void sendFallback_shouldEnqueueOutbox_withAggregateIdAndError() throws Exception {
        WorkloadOutboxPort outbox = mock(WorkloadOutboxPort.class);
        KafkaHeadersProvider headersProvider = mock(KafkaHeadersProvider.class);

        @SuppressWarnings("unchecked")
        KafkaTemplate<String, TrainerWorkloadRequestDto> kafkaTemplate = mock(KafkaTemplate.class);

        WorkloadServiceClientKafka svc = newSvc(outbox, headersProvider, kafkaTemplate, "trainer.workload.events");

        TrainerWorkloadRequestDto req = new TrainerWorkloadRequestDto();
        req.setTrainerUsername("john");
        req.setTrainingDate(LocalDate.of(2026, 2, 9));
        req.setTrainingDuration(45);

        RuntimeException ex = new RuntimeException("service down");

        // method name is sendFallback now (NOT "fallback")
        Method m = WorkloadServiceClientKafka.class
                .getDeclaredMethod("sendFallback", TrainerWorkloadRequestDto.class, Throwable.class);
        m.setAccessible(true);

        assertDoesNotThrow(() -> m.invoke(svc, req, ex));

        verify(outbox, times(1)).enqueue(
                eq(req),
                eq("john:2026-02-09"),
                contains("service down")
        );

        verifyNoInteractions(kafkaTemplate);
    }
}