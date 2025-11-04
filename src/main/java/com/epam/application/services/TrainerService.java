package com.epam.application.services;

import com.epam.model.Trainer;
import java.util.List;

public interface TrainerService {
    Trainer createTrainer(Trainer trainer);
    Trainer updateTrainer(Trainer trainer);
    Trainer getTrainerById(String trainerId);
    Trainer getTrainerByUserName(String username);
    List<Trainer> getAllTrainers();
}
