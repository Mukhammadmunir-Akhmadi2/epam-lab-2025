package com.epam.application.facade.impl;

import com.epam.application.exceptions.UnauthorizedAccess;
import com.epam.application.facade.TraineeProfileFacade;
import com.epam.application.provider.AuthProviderService;
import com.epam.application.services.BaseUserAuthService;
import com.epam.application.services.TraineeService;
import com.epam.application.services.TrainerQueryService;
import com.epam.application.services.TrainerService;
import com.epam.application.services.TrainingQueryService;
import com.epam.model.Trainee;
import com.epam.model.Trainer;
import com.epam.model.Training;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class TraineeProfileFacadeImpl implements TraineeProfileFacade {

    private final TraineeService traineeService;
    private final BaseUserAuthService authService;
    private final TrainerService trainerService;
    private final TrainerQueryService trainerQueryService;
    private final TrainingQueryService trainingQueryService;
    private final AuthProviderService authProviderService;

    @Override
    public Trainee registerTrainee(@Valid Trainee trainee) {
        return traineeService.createTrainee(trainee);
    }

    @Override
    public Trainee updateTraineeProfile(@Valid Trainee trainee) {
        isAuthenticated(trainee.getUserName());
        return traineeService.updateTrainee(trainee);
    }

    @Override
    public Trainee getTraineeProfile(String traineeUsername) {
        isAuthenticated(traineeUsername);
        return traineeService.getTraineeByUserName(traineeUsername);
    }

    @Override
    public void deleteTraineeProfile(String traineeUsername) {
        isAuthenticated(traineeUsername);
        traineeService.deleteTrainee(traineeUsername);
    }

    @Override
    public boolean toggleActive(String traineeUsername) {
        isAuthenticated(traineeUsername);
        return authService.toggleActive(traineeUsername);
    }

    @Override
    public String login(String traineeUsername, String password) {
        return authService.authenticateUser(traineeUsername, password);
    }

    @Override
    public void changePassword(String traineeUsername, String oldPassword, @Size(min = 6) String newPassword) {
        isAuthenticated(traineeUsername);

        authService.changePassword(traineeUsername, oldPassword, newPassword);
    }

    @Override
    public List<Trainer> updatTraineeTrainersList(String traineeUsername, @NotEmpty List<String> trainersUserNames) {
        isAuthenticated(traineeUsername);

        List<Trainer> assignedTrainers = trainersUserNames.stream()
                .map(trainerService::getTrainerByUserName)
                .toList();

        traineeService.updateTraineeTrainers(traineeUsername, assignedTrainers);
        return assignedTrainers;
    }

    @Override
    public List<Trainer> getActiveTrainersNotAssignedToTrainee(String traineeUsername) {
        isAuthenticated(traineeUsername);

        return trainerQueryService.getUnassignedTrainers(traineeUsername);
    }

    @Override
    public List<Training> getTraineeTrainings(String traineeUsername,
                                              LocalDate from,
                                              LocalDate to,
                                              String trainerUsername,
                                              String trainingType) {
        isAuthenticated(traineeUsername);

        return trainingQueryService.getTraineeTrainings(traineeUsername, from, to, trainerUsername, trainingType);
    }

    private void isAuthenticated(String traineeUsername) {
        if (!authProviderService.isAuthenticated(traineeUsername)) {
            throw new UnauthorizedAccess("Trainee " + traineeUsername + " is not authenticated.");
        }
    }
}
