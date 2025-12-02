package com.epam.infrastructure.controllers.Impl;

import com.epam.application.provider.AuthProviderService;
import com.epam.application.services.TraineeService;
import com.epam.application.services.TrainerService;
import com.epam.application.services.TrainingQueryService;
import com.epam.application.services.TrainingService;
import com.epam.application.services.TrainingTypeService;
import com.epam.infrastructure.controllers.TrainingController;
import com.epam.infrastructure.dtos.TraineeTrainingDto;
import com.epam.infrastructure.dtos.TrainerTrainingDto;
import com.epam.infrastructure.dtos.TrainingDto;
import com.epam.infrastructure.enums.TrainingTypeEnum;
import com.epam.infrastructure.mappers.TrainingMapper;
import com.epam.model.Trainee;
import com.epam.model.Trainer;
import com.epam.model.Training;
import com.epam.model.TrainingType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TrainingControllerImpl implements TrainingController {

    private final TrainingQueryService trainingQueryService;
    private final AuthProviderService authProviderService;
    private final TrainingService trainingService;
    private final TrainerService trainerService;
    private final TraineeService traineeService;
    private final TrainingTypeService trainingTypeService;
    private final TrainingMapper trainingMapper;


    @Override
    public ResponseEntity<Void> addTraining(String username, String trainerUsername, TrainingDto request) {
        authProviderService.ensureAuthenticated(username);

        Trainee trainee = traineeService.getTraineeByUserName(username);
        Trainer trainer = trainerService.getTrainerByUserName(trainerUsername);
        TrainingType trainingType = trainingTypeService.getTrainingType(request.getType());
        trainingService.createTraining(trainingMapper.toModel(trainee, trainer, trainingType, request));

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<TrainerTrainingDto>> getTraineeTrainings(String username, LocalDate from, LocalDate to, String trainerUsername, TrainingTypeEnum trainingType) {
        authProviderService.ensureAuthenticated(username);

        List<Training> trainings = trainingQueryService
                .getTraineeTrainings(username, from, to, trainerUsername, trainingType);
        List<TrainerTrainingDto> responseDto = trainingMapper.toTrainerTrainingDtoList(trainings);

        return ResponseEntity.ok().body(responseDto);
    }

    @Override
    public ResponseEntity<List<TraineeTrainingDto>> getTrainerTrainings(String username, LocalDate from, LocalDate to, String traineeUsername) {
        authProviderService.ensureAuthenticated(username);

        List<Training> trainings = trainingQueryService.getTrainerTrainings(username, from, to, traineeUsername);
        List<TraineeTrainingDto> responseDto = trainingMapper.toTraineeTrainingDtoList(trainings);

        return ResponseEntity.ok().body(responseDto);
    }
}
