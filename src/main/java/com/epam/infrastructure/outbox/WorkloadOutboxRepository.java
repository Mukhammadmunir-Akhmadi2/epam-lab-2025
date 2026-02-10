package com.epam.infrastructure.outbox;

import com.epam.infrastructure.outbox.entity.WorkloadOutboxEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WorkloadOutboxRepository {
    WorkloadOutboxEvent save(WorkloadOutboxEvent event);
    Optional<WorkloadOutboxEvent> findById(String id);
    List<WorkloadOutboxEvent> findBatchForRetry(LocalDateTime now);
    void delete(String id);
}