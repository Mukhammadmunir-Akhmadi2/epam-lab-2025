package com.epam.application.port;

import com.epam.model.Training;

import java.util.List;

public interface WorkloadEventPublisher {
    void publishTrainingAdded(Training training);
    void publishTrainingDeleted(Training training);
    void publishTrainingsDeleted(List<Training> trainings);
}