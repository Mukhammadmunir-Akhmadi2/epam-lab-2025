package com.epam.application.services;

import com.epam.infrastructure.enums.TrainingTypeEnum;
import com.epam.model.TrainingType;

import java.util.List;

public interface TrainingTypeService {
    TrainingType getTrainingType(TrainingTypeEnum type);
    List<TrainingType> getAllTrainingTypes();
}
