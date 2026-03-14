package com.epam.cucumber.client;

import com.epam.cucumber.contracts.TrainerTrainingSummaryResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "workload-service")
public interface WorkloadServiceClient {

    @GetMapping("/trainers/{username}/workload")
    ResponseEntity<TrainerTrainingSummaryResponseDto> getTrainerWorkload(
            @PathVariable("username") String username,
            @RequestHeader("Authorization") String authorization
    );
}