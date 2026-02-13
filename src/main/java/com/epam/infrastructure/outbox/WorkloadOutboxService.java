package com.epam.infrastructure.outbox;

import com.epam.infrastructure.dtos.TrainerWorkloadRequestDto;
import com.epam.infrastructure.enums.OutboxEventType;
import com.epam.infrastructure.enums.OutboxStatus;
import com.epam.infrastructure.outbox.entity.WorkloadOutboxEvent;
import com.epam.infrastructure.outbox.util.OutboxSerializer;
import com.epam.infrastructure.repository.JpaWorkloadOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Log4j2
@Service
@RequiredArgsConstructor
public class WorkloadOutboxService implements WorkloadOutboxPort {

    private static final int MAX_ATTEMPTS = 10;

    private final JpaWorkloadOutboxRepository repo;
    private final OutboxSerializer serializer;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void enqueue(TrainerWorkloadRequestDto req, String aggregateId, String error) {
        LocalDateTime now = LocalDateTime.now();
        String txId = MDC.get("transactionId");
        if (txId == null || txId.isBlank()) txId = "no-tx";

        WorkloadOutboxEvent event = new WorkloadOutboxEvent();
        event.setEventType(OutboxEventType.TRAINING_WORKLOAD);
        event.setStatus(OutboxStatus.NEW);
        event.setAttempts(0);
        event.setCreatedAt(now);
        event.setNextAttemptAt(now.plusSeconds(5));
        event.setTransactionId(txId);
        event.setAggregateId(aggregateId);
        event.setPayloadJson(serializer.toJson(req));
        event.setLastError(error);

        event = repo.save(event);

        log.info(
                "Outbox event enqueued. eventId{}, eventType={}, aggregateId={}, txId={}",
                event.getWoeId(), event.getEventType(), aggregateId, txId
        );
    }

    @Override
    @Transactional
    public void markSent(WorkloadOutboxEvent e) {
        e.setStatus(OutboxStatus.SENT);
        repo.save(e);

        log.info(
                "Outbox event sent. aggregateId={}, attempts={}",
                e.getAggregateId(), e.getAttempts()
        );
    }

    @Override
    @Transactional
    public void markFailed(WorkloadOutboxEvent e, String error) {
        int attempts = e.getAttempts() + 1;
        e.setAttempts(attempts);
        e.setLastError(truncate(error, 900));

        if (attempts >= MAX_ATTEMPTS) {
            e.setStatus(OutboxStatus.DEAD);
            e.setNextAttemptAt(LocalDateTime.now().plusYears(10));

            log.error(
                    "Outbox event moved to DEAD. aggregateId={}, attempts={}",
                     e.getAggregateId(), attempts
            );
        } else {
            e.setStatus(OutboxStatus.RETRY);
            e.setNextAttemptAt(LocalDateTime.now().plus(backoff(attempts)));

            log.warn(
                    "Outbox event retry scheduled. aggregateId={}, attempts={}, nextAttemptAt={}",
                    e.getAggregateId(), attempts, e.getNextAttemptAt()
            );
        }

        repo.save(e);
    }

    // exponential backoff with cap: 5s,10s,20s,... max 10 minutes
    private Duration backoff(int attempts) {
        long seconds = Math.min(600, (long) (5 * Math.pow(2, Math.max(0, attempts - 1))));
        return Duration.ofSeconds(seconds);
    }

    private String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max);
    }
}