package com.epam.infrastructure.outbox;

import com.epam.infrastructure.dtos.TrainerWorkloadRequestDto;
import com.epam.infrastructure.integration.KafkaHeadersProvider;
import com.epam.infrastructure.logging.TransactionIdFilter;
import com.epam.infrastructure.outbox.entity.WorkloadOutboxEvent;
import com.epam.infrastructure.outbox.util.OutboxSerializer;
import com.epam.infrastructure.repository.JpaWorkloadOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Log4j2
@Component
@RequiredArgsConstructor
@Profile({"local", "stg", "test", "prod"})
public class WorkloadOutboxPublisher {

    private final JpaWorkloadOutboxRepository repo;
    private final WorkloadOutboxPort outboxPort;
    private final OutboxSerializer serializer;
    private final KafkaTemplate<String, TrainerWorkloadRequestDto> kafkaTemplate;
    private final KafkaHeadersProvider headersProvider;

    @Value("${app.kafka.topics.workload-events}")
    private String topic;

    @Scheduled(fixedDelayString = "${outbox.workload.fixedDelayMs:5000}")
    public void publishBatch() {
        List<WorkloadOutboxEvent> batch = repo.findBatchForRetry(LocalDateTime.now());
        if (batch.isEmpty()) return;

        for (WorkloadOutboxEvent e : batch) {
            publishSingleInNewTx(e);
        }
    }

    public void publishSingleInNewTx(WorkloadOutboxEvent e) {
        if (e == null) return;
        TrainerWorkloadRequestDto req = null;
        try {
            req = serializer.fromMap(e.getPayload(), TrainerWorkloadRequestDto.class);
        } catch (Exception ex) {
            outboxPort.markFailed(e, "Deserialization error: " + ex.toString());
            log.warn("Outbox republish FAILED id={} attempts={} cause={}", e.getWoeId(), e.getAttempts(), ex.toString());
            return;
        }

        String key = req.getTrainerUsername();
        String txId = e.getTransactionId();
        String auth = headersProvider.innerServerAuthorizationValue();

        Message<TrainerWorkloadRequestDto> message = MessageBuilder
                .withPayload(req)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.KEY, key)
                .setHeader(TransactionIdFilter.TRANSACTION_ID_HEADER, txId)
                .setHeader(KafkaHeadersProvider.AUTH_HEADER, auth)
                .build();

        CompletableFuture<SendResult<String, TrainerWorkloadRequestDto>> future = null;

        try {
            future = kafkaTemplate.send(message);
        } catch (Exception ex) {
            outboxPort.markFailed(e, rootCause(ex).toString());
            log.warn("Outbox republish FAILED id={} attempts={} cause={}", e.getWoeId(), e.getAttempts(), ex.toString());
        }

        future.whenComplete((res, ex) -> {
            if (ex == null) {
                outboxPort.markSent(e);
                var m = res.getRecordMetadata();
                log.info("Outbox republished OK id={} attempts={} topic={} partition={} offset={}",
                        e.getWoeId(), e.getAttempts(),
                        res.getRecordMetadata().topic(),
                        res.getRecordMetadata().partition(),
                        res.getRecordMetadata().offset());
            } else {
                outboxPort.markFailed(e, ex.toString());
                log.warn("Outbox republish FAILED id={} cause={}", e.getWoeId(), ex.toString());
            }
        }).thenApply(ignored -> null);
    }

    private Throwable rootCause(Throwable ex) {
        return (ex.getCause() != null) ? ex.getCause() : ex;
    }
}