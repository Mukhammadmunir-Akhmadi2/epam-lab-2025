package com.epam.application.repository;

import com.epam.model.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeRepository {
    Trainee save(Trainee trainee);
    Optional<Trainee> findById(String traineeId);
    Optional<Trainee> findByUserName(String userName);
    List<Trainee> findAll();
    void delete(String traineeId);
}
