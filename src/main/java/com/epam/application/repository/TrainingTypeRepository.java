package com.epam.application.repository;

import com.epam.infrastructure.enums.TrainingTypeEnum;
import com.epam.model.TrainingType;

import java.util.List;
import java.util.Optional;

public interface TrainingTypeRepository {
    Optional<TrainingType> findByType(TrainingTypeEnum type);
    TrainingType save(TrainingType trainingType);
    List<TrainingType> findAll();
}
