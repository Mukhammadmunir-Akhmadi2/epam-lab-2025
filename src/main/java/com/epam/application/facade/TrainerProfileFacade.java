package com.epam.application.facade;

import com.epam.model.Trainer;
import com.epam.model.Training;

import java.time.LocalDate;
import java.util.List;

public interface TrainerProfileFacade {
    Trainer trainerRegistration(Trainer trainer);
    Trainer updateTrainerProfile(Trainer trainer);
    Trainer getTrainerProfile(String trainerUsername);
    String toggleActive(String trainerUsername);
    String login(String trainerUsername, String password);
    void changePassword(String trainerUsername, String oldPassword, String newPassword);
    void addTraining(String trainerUsername, String traineeUserName, Training training);
    List<Training> getTrainerTrainings(
            String trainerUsername,
            LocalDate from,
            LocalDate to,
            String traineeUserName);
}
