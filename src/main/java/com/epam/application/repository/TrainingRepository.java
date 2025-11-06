package com.epam.application.repository;

import com.epam.model.Training;

import java.util.List;
import java.util.Optional;

public interface TrainingRepository {
    Training save(Training training);
    Optional<Training> findById(String trainingId);
    List<Training> findAll();
}
