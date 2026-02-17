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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class WorkloadOutboxPublisher {

    private final JpaWorkloadOutboxRepository repo;
    private final WorkloadOutboxPort outboxPort;
    private final OutboxSerializer serializer;
    private final KafkaTemplate<String, TrainerWorkloadRequestDto> kafkaTemplate;
    private final KafkaHeadersProvider headersProvider;

    @Value("${app.kafka.topics.workload-events}")
    private String topic;

    @Scheduled(fixedDelayString = "${outbox.workload.fixedDelayMs:5000}")
    @Transactional
    public void publishBatch() {
        List<WorkloadOutboxEvent> batch = repo.findBatchForRetry(LocalDateTime.now());
        if (batch.isEmpty()) return;

        for (WorkloadOutboxEvent e : batch) {
            publishSingleInNewTx(e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void publishSingleInNewTx(WorkloadOutboxEvent e) {
        if (e == null) return;

        try {
            TrainerWorkloadRequestDto req =
                    serializer.fromJson(e.getPayloadJson(), TrainerWorkloadRequestDto.class);

            String key = req.getTrainerUsername();
            String txId = e.getTransactionId();
            String auth = headersProvider.currentAuthorizationValue();

            Message<TrainerWorkloadRequestDto> message = MessageBuilder
                    .withPayload(req)
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .setHeader(KafkaHeaders.KEY, key)
                    .setHeader(TransactionIdFilter.TRANSACTION_ID_HEADER, txId)
                    .setHeader(KafkaHeadersProvider.AUTH_HEADER, auth)
                    .build();

            var res = kafkaTemplate.send(message).get();

            outboxPort.markSent(e);
            log.info("Outbox republished OK id={} attempts={} topic={} partition={} offset={}",
                    e.getWoeId(), e.getAttempts(),
                    res.getRecordMetadata().topic(),
                    res.getRecordMetadata().partition(),
                    res.getRecordMetadata().offset());
        } catch (Exception ex) {
            outboxPort.markFailed(e, ex.toString());
            log.warn("Outbox republish FAILED id={} attempts={} cause={}", e.getWoeId(), e.getAttempts(), ex.toString());
        }
    }
}