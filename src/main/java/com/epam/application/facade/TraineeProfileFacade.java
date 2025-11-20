package com.epam.application.facade;

import com.epam.model.Trainee;
import com.epam.model.Trainer;
import com.epam.model.Training;

import java.time.LocalDate;
import java.util.List;

public interface TraineeProfileFacade {
    Trainee registerTrainee(Trainee trainee);
    Trainee updateTraineeProfile(Trainee trainee);
    Trainee getTraineeProfile(String traineeUsername);
    void deleteTraineeProfile(String traineeUsername);
    boolean toggleActive(String traineeUsername);
    String login(String traineeUsername, String password);
    void changePassword(String traineeUsername, String oldPassword, String newPassword);
    List<Trainer> updatTraineeTrainersList(String traineeUsername, List<String> trainersUserNames);
    List<Trainer> getActiveTrainersNotAssignedToTrainee(String traineeUsername);
    List<Training> getTraineeTrainings(
            String traineeUsername,
            LocalDate from,
            LocalDate to,
            String trainerUsername,
            String trainingType
    );
}
