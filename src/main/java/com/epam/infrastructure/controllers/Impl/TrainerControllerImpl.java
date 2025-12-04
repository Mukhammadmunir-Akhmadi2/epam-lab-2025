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
import com.epam.model.Trainer;
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
        Trainer saved = trainerService.createTrainer(trainerMapper.toModel(trainer, specialization));
        AuthDto authDto = trainerMapper.toAuthDto(saved);

        return ResponseEntity.status(HttpStatus.CREATED).body(authDto);
    }

    @Override
    public ResponseEntity<TrainerResponseDto> updateProfile(String username, TrainerDto trainer) {
        authProvider.ensureAuthenticated(username);

        trainer.setUsername(username);

        TrainingType trainingType = trainingTypeService.getTrainingType(trainer.getSpecialization());
        Trainer updated = trainerService.updateTrainer(trainerMapper.toModel(trainer, trainingType));
        TrainerResponseDto responseDto = trainerFullMapper.toTrainerResponseDto(updated);

        return ResponseEntity.ok().body(responseDto);
    }

    @Override
    public ResponseEntity<TrainerResponseDto> getProfile(String username) {
        authProvider.ensureAuthenticated(username);

        Trainer trainer = trainerService.getTrainerByUserName(username);
        TrainerResponseDto responseDto = trainerFullMapper.toTrainerResponseDto(trainer);

        return ResponseEntity.ok().body(responseDto);
    }
}
