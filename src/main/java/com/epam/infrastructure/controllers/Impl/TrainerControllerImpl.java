package com.epam.infrastructure.controllers.Impl;

import com.epam.application.provider.AuthProviderService;
import com.epam.application.services.TrainerService;
import com.epam.application.services.TrainingTypeService;
import com.epam.infrastructure.controllers.TrainerController;
import com.epam.infrastructure.dtos.TrainerDto;
import com.epam.infrastructure.dtos.TrainerRegistrationRequest;
import com.epam.infrastructure.dtos.TrainerResponseDto;
import com.epam.infrastructure.dtos.AuthDto;
import com.epam.infrastructure.mappers.TrainerFullMapper;
import com.epam.infrastructure.mappers.TrainerMapper;
import com.epam.model.TrainingType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TrainerControllerImpl implements TrainerController {

    private final TrainerService trainerService;
    private final TrainingTypeService trainingTypeService;
    private final AuthProviderService authProvider;
    private final TrainerMapper trainerMapper;
    private final TrainerFullMapper trainerFullMapper;

    @Override
    public ResponseEntity<AuthDto> register(TrainerRegistrationRequest trainer) {
        TrainingType specialization = trainingTypeService.getTrainingType(trainer.getSpecialization());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                trainerMapper.toAuthDto(
                        trainerService.createTrainer(trainerMapper.toModel(trainer, specialization))
                )
        );
    }

    @Override
    public ResponseEntity<TrainerResponseDto> updateProfile(TrainerDto trainer) {
        authProvider.ensureAuthenticated(trainer.getUsername());

        TrainingType trainingType = trainingTypeService.getTrainingType(trainer.getSpecialization());

        return ResponseEntity.ok().body(
                trainerFullMapper.toTrainerResponseDto(
                        trainerService.updateTrainer(
                                trainerMapper.toModel(trainer, trainingType))
                )
        );
    }

    @Override
    public ResponseEntity<TrainerResponseDto> getProfile(String username) {
        authProvider.ensureAuthenticated(username);

        return ResponseEntity.ok().body(
                trainerFullMapper.toTrainerResponseDto(
                        trainerService.getTrainerByUserName(username)
                )
        );
    }
}
