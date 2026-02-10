package com.epam.infrastructure.outbox;

import com.epam.infrastructure.integration.WorkloadClient;
import com.epam.infrastructure.dtos.TrainerWorkloadRequestDto;
import com.epam.infrastructure.outbox.entity.WorkloadOutboxEvent;
import com.epam.infrastructure.outbox.util.OutboxSerializer;
import com.epam.infrastructure.repository.JpaWorkloadOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
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
    private final WorkloadClient workloadClient;

    @Scheduled(fixedDelayString = "${outbox.workload.fixedDelayMs:5000}")
    @Transactional
    public void publishBatch() {
        List<WorkloadOutboxEvent> batch = repo.findBatchForRetry(LocalDateTime.now());
        if (batch.isEmpty()) return;

        for (WorkloadOutboxEvent e : batch) {
            try {
                MDC.put("transactionId", e.getTransactionId());

                TrainerWorkloadRequestDto req = serializer.fromJson(e.getPayloadJson(), TrainerWorkloadRequestDto.class);
                workloadClient.send(req);

                outboxPort.markSent(e);
                log.info("Outbox sent id={} attempts={}", e.getWoeId(), e.getAttempts());
            } catch (Exception ex) {
                outboxPort.markFailed(e, ex.toString());
                log.warn("Outbox send failed id={} attempts={} cause={}", e.getWoeId(), e.getAttempts(), ex.toString());
            } finally {
                MDC.remove("transactionId");
            }
        }
    }
}