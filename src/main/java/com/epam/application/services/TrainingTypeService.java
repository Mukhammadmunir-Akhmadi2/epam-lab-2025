package com.epam.application.services;

import com.epam.model.TrainingType;

public interface TrainingTypeService {
    TrainingType findOrCreate(String typeName);
}
