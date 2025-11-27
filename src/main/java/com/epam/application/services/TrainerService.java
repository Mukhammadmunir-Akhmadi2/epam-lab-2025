package com.epam.application.services;

import com.epam.model.Trainer;
import jakarta.validation.Valid;

public interface TrainerService {
    Trainer createTrainer(@Valid Trainer trainer);
    Trainer updateTrainer(@Valid Trainer trainer);
    Trainer getTrainerById(String trainerId);
    Trainer getTrainerByUserName(String username);
}
