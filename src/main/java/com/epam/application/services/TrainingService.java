package com.epam.application.services;

import com.epam.model.Training;
import jakarta.validation.Valid;

import java.util.List;

public interface TrainingService {
    Training createTraining(@Valid Training training);
    Training getTrainingById(String trainingId);
    List<Training> getAllTrainings();
}
