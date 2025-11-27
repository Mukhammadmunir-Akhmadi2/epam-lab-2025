package com.epam.infrastructure.controllers.Impl;

import com.epam.application.provider.AuthProviderService;
import com.epam.application.services.TraineeService;
import com.epam.application.services.TrainerQueryService;
import com.epam.application.services.TrainerService;
import com.epam.infrastructure.controllers.TraineeController;
import com.epam.infrastructure.dtos.TraineeDto;
import com.epam.infrastructure.dtos.TraineeRegistrationRequest;
import com.epam.infrastructure.dtos.TraineeResponseDto;
import com.epam.infrastructure.dtos.TrainerBriefDto;
import com.epam.infrastructure.dtos.AuthDto;
import com.epam.infrastructure.mappers.TraineeFullMapper;
import com.epam.infrastructure.mappers.TraineeMapper;
import com.epam.infrastructure.mappers.TrainerMapper;
import com.epam.model.Trainer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TraineeControllerImpl implements TraineeController {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainerQueryService trainerQueryService;
    private final AuthProviderService authProvider;
    private final TraineeMapper traineeMapper;
    private final TraineeFullMapper traineeFullMapper;
    private final TrainerMapper trainerMapper;


    @Override
    public ResponseEntity<AuthDto> register(TraineeRegistrationRequest trainee) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                traineeMapper.toAuthDto(
                        traineeService.createTrainee(
                                traineeMapper.toModel(trainee)
                        )
                )
        );
    }

    @Override
    public ResponseEntity<TraineeResponseDto> updateProfile(TraineeDto trainee) {
        authProvider.ensureAuthenticated(trainee.getUsername());

        return ResponseEntity.ok().body(
                traineeFullMapper.toTraineeResponseDto(
                        traineeService.updateTrainee(
                                traineeMapper.toModel(trainee)
                        )
                )
        );
    }

    @Override
    public ResponseEntity<TraineeResponseDto> getProfile(String username) {
        authProvider.ensureAuthenticated(username);
        return ResponseEntity.ok().body(
                traineeFullMapper.toTraineeResponseDto(
                        traineeService.getTraineeByUserName(username)
                )
        );
    }

    @Override
    public ResponseEntity<Void> deleteProfile(String username) {
        authProvider.ensureAuthenticated(username);
        traineeService.deleteTrainee(username);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<TrainerBriefDto>> getUnassignedActiveTrainers(String username) {
        authProvider.ensureAuthenticated(username);

        return ResponseEntity.ok().body(
                trainerMapper.toTrainerBriefDtoList(
                        trainerQueryService.getUnassignedTrainers(username)
                )
        );
    }

    @Override
    public ResponseEntity<List<TrainerBriefDto>> updateTraineeTrainersList(String username, List<String> trainers) {
        authProvider.ensureAuthenticated(username);

        List<Trainer> assignedTrainers = trainers.stream()
                .map(trainerService::getTrainerByUserName)
                .toList();


        return ResponseEntity.ok().body(
                trainerMapper.toTrainerBriefDtoList(
                        traineeService.updateTraineeTrainers(username, assignedTrainers)
                )
        );
    }
}
