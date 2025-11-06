package com.epam.application.services;

import com.epam.model.Training;
import java.util.List;

public interface TrainingService {
    Training createTraining(Training training);
    Training getTrainingById(String trainingId);
    List<Training> getAllTrainings();
}
