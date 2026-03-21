package com.epam.cucumber.client;

import com.epam.infrastructure.dtos.AuthDto;
import com.epam.infrastructure.dtos.TokenDto;
import com.epam.infrastructure.dtos.TraineeRegistrationRequest;
import com.epam.infrastructure.dtos.TrainerRegistrationRequest;
import com.epam.infrastructure.dtos.TrainingDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "training-service")
public interface TrainingServiceClient {

    @PostMapping("/users/login")
    ResponseEntity<TokenDto> login(@RequestBody AuthDto request);

    @PostMapping("/trainers")
    ResponseEntity<AuthDto> createTrainer(@RequestBody TrainerRegistrationRequest request);

    @PostMapping("/trainees")
    ResponseEntity<AuthDto> createTrainee(@RequestBody TraineeRegistrationRequest request);

    @PostMapping("/trainings/trainee/{username}/trainer/{trainerUsername}")
    ResponseEntity<Void> createTraining(@PathVariable("username") String traineeUsername,
                                        @PathVariable("trainerUsername") String trainerUsername,
                                        @RequestBody TrainingDto request,
                                        @RequestHeader("Authorization") String token);

    @DeleteMapping("/trainees/{username}")
    ResponseEntity<Void> deleteTrainee(@PathVariable("username") String traineeUsername,
                                       @RequestHeader("Authorization") String token);
}