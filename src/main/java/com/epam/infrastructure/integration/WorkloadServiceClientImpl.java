package com.epam.infrastructure.integration;

import com.epam.infrastructure.outbox.WorkloadOutboxPort;
import com.epam.application.port.WorkloadServiceClient;
import com.epam.infrastructure.dtos.TrainerWorkloadRequestDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class WorkloadServiceClientImpl implements WorkloadServiceClient {
    private final WorkloadClient client;
    private final WorkloadOutboxPort outboxPort;


    @Override
    @CircuitBreaker(name = "workload", fallbackMethod = "fallback")
    public void send(TrainerWorkloadRequestDto req) {
        client.send(req);
    }

    private void fallback(TrainerWorkloadRequestDto req, Throwable ex) {
        log.warn("Workload service unavailable. trainer={} date={} duration={} cause={}",
                        req.getTrainerUsername(), req.getTrainingDate(), req.getTrainingDuration(), ex.toString());

        String aggregateId = req.getTrainerUsername() + ":" + req.getTrainingDate();
        outboxPort.enqueue(req, aggregateId, ex.toString());
    }
}