package com.epam.application.services.impl;

import com.epam.application.repository.TrainingQueryRepository;
import com.epam.application.services.TrainingQueryService;
import com.epam.infrastructure.enums.TrainingTypeEnum;
import com.epam.model.Training;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingQueryServiceImpl implements TrainingQueryService {

    private final TrainingQueryRepository trainingQueryRepository;

    @Override
    public List<Training> getTraineeTrainings(String traineeUsername, LocalDate from, LocalDate to, String trainerName, TrainingTypeEnum trainingType) {
        return trainingQueryRepository
                .findTrainingsByTraineeUsernameWithFilters(traineeUsername, from, to, trainerName, trainingType);
    }

    @Override
    public List<Training> getTrainerTrainings(String trainerUsername, LocalDate from, LocalDate to, String traineeName) {
        return trainingQueryRepository.findTrainingsByTrainerUsernameWithFilters(trainerUsername, from, to, traineeName);
    }
}
