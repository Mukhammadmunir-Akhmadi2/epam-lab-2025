package com.epam.application.repository;

import com.epam.infrastructure.enums.TrainingTypeEnum;
import com.epam.model.Training;

import java.time.LocalDate;
import java.util.List;

public interface TrainingQueryRepository {
    List<Training> findTrainingsByTraineeUsernameWithFilters(
            String traineeUsername,
            LocalDate from,
            LocalDate to,
            String trainerName,
            TrainingTypeEnum trainingType
    );
    List<Training> findTrainingsByTrainerUsernameWithFilters(
            String trainerUsername,
            LocalDate from,
            LocalDate to,
            String traineeName);

}
