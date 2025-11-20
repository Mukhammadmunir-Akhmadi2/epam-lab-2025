package com.epam.application.facade.impl;

import com.epam.application.exceptions.UnauthorizedAccess;
import com.epam.application.facade.TrainerProfileFacade;
import com.epam.application.provider.AuthProviderService;
import com.epam.application.services.BaseUserAuthService;
import com.epam.application.services.TraineeService;
import com.epam.application.services.TrainerService;
import com.epam.application.services.TrainingQueryService;
import com.epam.application.services.TrainingService;
import com.epam.application.services.TrainingTypeService;
import com.epam.model.Trainee;
import com.epam.model.Trainer;
import com.epam.model.Training;
import com.epam.model.TrainingType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class TrainerProfileFacadeImpl implements TrainerProfileFacade {

    private final TrainerService trainerService;
    private final BaseUserAuthService authService;
    private final TrainingService trainingService;
    private final TrainingQueryService trainingQueryService;
    private final AuthProviderService authProviderService;
    private final TraineeService traineeService;
    private final TrainingTypeService trainingTypeService;

    @Override
    public Trainer registerTrainer(@Valid Trainer trainer) {
        TrainingType specialization = trainingTypeService.getTrainingType(trainer.getSpecialization().getTrainingType());
        trainer.setSpecialization(specialization);
        return trainerService.createTrainer(trainer);
    }

    @Override
    public Trainer updateTrainerProfile(@Valid Trainer trainer) {
        isAuthenticated(trainer.getUserName());
        return trainerService.updateTrainer(trainer);
    }

    @Override
    public Trainer getTrainerProfile(String trainerUsername) {
        isAuthenticated(trainerUsername);

        return trainerService.getTrainerByUserName(trainerUsername);
    }

    @Override
    public boolean toggleActive(String trainerUsername) {
        isAuthenticated(trainerUsername);

        return authService.toggleActive(trainerUsername);
    }

    @Override
    public String login(String trainerUsername, String password) {
        return  authService.authenticateUser(trainerUsername, password);
    }

    @Override
    public void changePassword(String trainerUsername, String oldPassword, @Size(min = 6) String newPassword) {
        isAuthenticated(trainerUsername);

        authService.changePassword(trainerUsername, oldPassword, newPassword);
    }

    @Override
    public void addTraining(String trainerUsername, String traineeUsername, @Valid Training training) {
        isAuthenticated(trainerUsername);

        Trainer trainer = trainerService.getTrainerByUserName(trainerUsername);
        training.setTrainer(trainer);

        Trainee trainee = traineeService.getTraineeByUserName(traineeUsername);
        training.setTrainee(trainee);

        TrainingType trainingType = trainingTypeService.getTrainingType(training.getTrainingType().getTrainingType());
        training.setTrainingType(trainingType);

        trainingService.createTraining(training);
    }

    @Override
    public List<Training> getTrainerTrainings(String trainerUsername, LocalDate from, LocalDate to, String traineeUsername) {
        isAuthenticated(trainerUsername);

        return trainingQueryService.getTrainerTrainings(trainerUsername, from, to, traineeUsername);
    }

    private void isAuthenticated(String trainerUsername) {
        if (!authProviderService.isAuthenticated(trainerUsername)) {
            throw new UnauthorizedAccess("Trainee " + trainerUsername + " is not authenticated.");
        }
    }
}
