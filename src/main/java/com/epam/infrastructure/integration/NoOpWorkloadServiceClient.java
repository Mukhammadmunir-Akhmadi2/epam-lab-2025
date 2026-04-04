package com.epam.infrastructure.integration;

import com.epam.application.port.WorkloadServiceClient;
import com.epam.infrastructure.dtos.TrainerWorkloadRequestDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@Profile("dev")
public class NoOpWorkloadServiceClient implements WorkloadServiceClient {

    @Override
    public void send(TrainerWorkloadRequestDto req) {
        log.info("[DEV] Skipping workload event publish. trainer={} action={}",
                req.getTrainerUsername(), req.getActionType());
    }
}