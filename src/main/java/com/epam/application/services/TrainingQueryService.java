package com.epam.application.services;

import com.epam.model.Training;

import java.time.LocalDate;
import java.util.List;

public interface TrainingQueryService {
    List<Training> getTraineeTrainings(
            String traineeUsername,
            LocalDate from,
            LocalDate to,
            String trainerUsername,
            String trainingType
    );
    List<Training> getTrainerTrainings(
            String trainerUsername,
            LocalDate from,
            LocalDate to,
            String traineeUsername);
}
