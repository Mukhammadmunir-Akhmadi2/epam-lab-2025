package com.epam.application.services;

import com.epam.model.Trainee;
import com.epam.model.Trainer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public interface TraineeService {
    Trainee createTrainee(@Valid Trainee trainee);
    Trainee updateTrainee(@Valid Trainee trainee);
    void deleteTrainee(String traineeId);
    Trainee getTraineeById(String traineeId);
    Trainee getTraineeByUserName(String traineeUsername);
    List<Trainer> updateTraineeTrainers(String traineeUsername, @NotEmpty List<Trainer> trainers);
}
