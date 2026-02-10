package com.epam.infrastructure.integration;

import com.epam.infrastructure.config.WorkloadFeignConfig;
import com.epam.infrastructure.dtos.TrainerWorkloadRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "workload-service", configuration = WorkloadFeignConfig.class)
public interface WorkloadClient {
    @PostMapping("/workload-events")
    void send(@RequestBody TrainerWorkloadRequestDto req);
}