package com.epam.application.facade.impl;

import com.epam.application.exceptions.InvalidCredentialException;
import com.epam.application.exceptions.UnauthorizedAccess;
import com.epam.application.facade.TrainerProfileFacade;
import com.epam.application.services.TraineeService;
import com.epam.application.services.TrainerAuthService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Validated
public class TrainerProfileFacadeImpl implements TrainerProfileFacade {

    private final TrainerService trainerService;
    private final TrainerAuthService trainerAuthService;
    private final TrainingService trainingService;
    private final TrainingTypeService trainingTypeService;
    private final TrainingQueryService trainingQueryService;

    private final Map<String, Trainer> authenticatedSessions = new HashMap<>();
    private final TraineeService traineeService;

    @Override
    public Trainer trainerRegistration(@Valid Trainer trainer) {
        TrainingType specialization = trainingTypeService.findOrCreate(trainer.getSpecialization().getTrainingType());

        trainer.setSpecialization(specialization);

        return trainerService.createTrainer(trainer);
    }

    @Override
    public Trainer updateTrainerProfile(@Valid Trainer trainer) {
        isAuthenticated(trainer.getUserName());

        TrainingType specialization = trainingTypeService.findOrCreate(trainer.getSpecialization().getTrainingType());

        trainer.setSpecialization(specialization);
        return trainerService.updateTrainer(trainer);
    }

    @Override
    public Trainer getTrainerProfile(String trainerUsername) {
        isAuthenticated(trainerUsername);

        return trainerService.getTrainerByUserName(trainerUsername);
    }

    @Override
    public String toggleActive(String trainerUsername) {
        isAuthenticated(trainerUsername);

        return trainerAuthService.toggleActive(trainerUsername);
    }

    @Override
    public String login(String trainerUsername, String password) {
        Trainer trainer = trainerService.getTrainerByUserName(trainerUsername);

        if (!trainer.getPassword().equals(password)) {
            throw new InvalidCredentialException("Invalid password for trainer " + trainerUsername);
        }

        authenticatedSessions.put(trainerUsername, trainer);
        return "Trainer " + trainerUsername + " authenticated successfully.";
    }

    @Override
    public void changePassword(String trainerUsername, String oldPassword, @Size(min = 6) String newPassword) {
        isAuthenticated(trainerUsername);

        trainerAuthService.changePassword(trainerUsername, oldPassword, newPassword);
    }

    @Override
    public void addTraining(String trainerUsername, String traineeUsername, @Valid Training training) {
        isAuthenticated(trainerUsername);

        Trainer trainer = trainerService.getTrainerByUserName(trainerUsername);
        training.setTrainer(trainer);

        Trainee trainee = traineeService.getTraineeByUserName(traineeUsername);
        training.setTrainee(trainee);

        TrainingType trainingType = trainingTypeService.findOrCreate(training.getTrainingType().getTrainingType());
        training.setTrainingType(trainingType);

        trainingService.createTraining(training);
    }

    @Override
    public List<Training> getTrainerTrainings(String trainerUsername, LocalDate from, LocalDate to, String traineeUsername) {
        isAuthenticated(trainerUsername);

        return trainingQueryService.getTrainerTrainings(trainerUsername, from, to, traineeUsername);
    }

    private void isAuthenticated(String trainerUsername) {
        if (!authenticatedSessions.containsKey(trainerUsername)) {
            throw new UnauthorizedAccess("Trainer " + trainerUsername + " is not authenticated.");
        }
    }
}
