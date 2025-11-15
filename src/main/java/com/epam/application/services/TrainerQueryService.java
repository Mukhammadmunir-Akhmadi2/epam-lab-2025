package com.epam.application.services;

import com.epam.model.Trainer;

import java.util.List;

public interface TrainerQueryService {
    List<Trainer> getUnassignedTrainers(String traineeUsername);
}
