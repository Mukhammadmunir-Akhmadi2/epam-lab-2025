package com.epam.application.services;

import com.epam.infrastructure.enums.TrainingTypeEnum;
import com.epam.model.TrainingType;

public interface TrainingTypeService {
    TrainingType getTrainingType(TrainingTypeEnum type);
}
