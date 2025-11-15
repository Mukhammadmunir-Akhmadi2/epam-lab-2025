package com.epam.application.services;

import com.epam.model.Trainee;
import com.epam.model.Trainer;

import java.util.List;


public interface TraineeService {
    Trainee createTrainee(Trainee trainee);
    Trainee updateTrainee(Trainee trainee);
    void deleteTrainee(String traineeId);
    Trainee getTraineeById(String traineeId);
    Trainee getTraineeByUserName(String traineeUsername);
    void updateTraineeTrainers(String traineeUsername, List<Trainer> trainers);
}
