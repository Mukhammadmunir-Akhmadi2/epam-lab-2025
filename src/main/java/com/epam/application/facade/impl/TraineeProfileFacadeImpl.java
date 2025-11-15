package com.epam.application.facade.impl;

import com.epam.application.exceptions.InvalidCredentialException;
import com.epam.application.exceptions.UnauthorizedAccess;
import com.epam.application.facade.TraineeProfileFacade;
import com.epam.application.services.TraineeAuthService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Validated
public class TraineeProfileFacadeImpl implements TraineeProfileFacade {

    private final TraineeService traineeService;
    private final TraineeAuthService traineeAuthService;
    private final TrainerService trainerService;
    private final TrainerQueryService trainerQueryService;
    private final TrainingQueryService trainingQueryService;

    private final Map<String, Trainee> authenticatedSessions = new HashMap<>();

    @Override
    public Trainee traineeRegistration(@Valid Trainee trainee) {
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
    public String toggleActive(String traineeUsername) {
        isAuthenticated(traineeUsername);
        return traineeAuthService.toggleActive(traineeUsername);
    }

    @Override
    public String login(String traineeUsername, String password) {
        Trainee trainee = traineeService.getTraineeByUserName(traineeUsername);

        if (!trainee.getPassword().equals(password)) {
            throw new InvalidCredentialException("Invalid password for trainee " + traineeUsername);
        }
        authenticatedSessions.put(traineeUsername, trainee);
        return "Trainee " + traineeUsername + " authenticated successfully.";
    }

    @Override
    public void changePassword(String traineeUsername, String oldPassword, @Size(min = 6) String newPassword) {
        isAuthenticated(traineeUsername);

        traineeAuthService.changePassword(traineeUsername, oldPassword, newPassword);
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

    private void isAuthenticated(String username) {
        if (!authenticatedSessions.containsKey(username)) {
            throw new UnauthorizedAccess("Trainee " + username + " is not authenticated.");
        };
    }
}
