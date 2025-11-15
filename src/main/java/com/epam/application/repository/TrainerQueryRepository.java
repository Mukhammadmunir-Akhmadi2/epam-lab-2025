package com.epam.application.repository;

import com.epam.model.Trainer;

import java.util.List;

public interface TrainerQueryRepository {
    List<Trainer> findUnassignedTrainersByTraineeUsername(String traineeUsername);
}
