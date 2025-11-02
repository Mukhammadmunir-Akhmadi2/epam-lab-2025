package com.epam.application.repository;

import com.epam.model.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerRepository {
    Trainer save(Trainer trainer);
    Optional<Trainer> findById(String trainerId);
    Optional<Trainer> findByUserName(String userName);
    List<Trainer> findAll();
}
