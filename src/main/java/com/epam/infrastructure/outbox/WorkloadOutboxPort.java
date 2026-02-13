package com.epam.infrastructure.outbox;

import com.epam.infrastructure.dtos.TrainerWorkloadRequestDto;
import com.epam.infrastructure.outbox.entity.WorkloadOutboxEvent;

public interface WorkloadOutboxPort {
    void enqueue(TrainerWorkloadRequestDto req, String aggregateId, String error);
    void markSent(WorkloadOutboxEvent e);
    void markFailed(WorkloadOutboxEvent e, String error);
}
