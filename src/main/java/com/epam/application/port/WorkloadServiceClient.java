package com.epam.application.port;

import com.epam.infrastructure.dtos.TrainerWorkloadRequestDto;

public interface WorkloadServiceClient {
    void send(TrainerWorkloadRequestDto req);
}
