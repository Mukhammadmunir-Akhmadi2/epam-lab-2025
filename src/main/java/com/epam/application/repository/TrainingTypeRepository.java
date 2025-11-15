package com.epam.application.repository;

import com.epam.model.TrainingType;

import java.util.Optional;

public interface TrainingTypeRepository {
    Optional<TrainingType> findByType(String type);
    TrainingType save(TrainingType trainingType);
}
