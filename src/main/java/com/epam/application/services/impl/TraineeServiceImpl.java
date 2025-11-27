package com.epam.application.services.impl;

import com.epam.application.exceptions.ResourceNotFoundException;
import com.epam.application.generators.PasswordGenerator;
import com.epam.application.generators.UsernameGenerator;
import com.epam.application.repository.BaseUserRepository;
import com.epam.application.repository.TraineeRepository;
import com.epam.application.services.TraineeService;
import com.epam.model.Trainee;
import com.epam.model.Trainer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {
    private static final Logger log = LoggerFactory.getLogger(TraineeServiceImpl.class);

    private final TraineeRepository traineeRepository;
    private final BaseUserRepository baseUserRepository;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;


    @Transactional
    @Override
    public Trainee createTrainee(@Valid Trainee trainee) {
        String username = usernameGenerator
                .generateUsername(trainee, name -> baseUserRepository.findByUserName(name).isPresent());
        String password = passwordGenerator.generatePassword(10);

        trainee.setUsername(username);
        trainee.setPassword(password);
        trainee.setActive(true);

        Trainee saved = traineeRepository.save(trainee);
        log.info("Created trainee id={} username={}", saved.getUserId(), saved.getUsername());
        return saved;
    }

    @Transactional
    @Override
    public Trainee updateTrainee(@Valid Trainee trainee) {
        Trainee existing = traineeRepository.findByUserName(trainee.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Trainee not found username=" + trainee.getUsername()));

        existing.setFirstName(trainee.getFirstName());
        existing.setLastName(trainee.getLastName());

        existing.setDateOfBirth(trainee.getDateOfBirth());
        existing.setAddress(trainee.getAddress());
        existing.setActive(trainee.isActive());

        Trainee updated = traineeRepository.save(existing);

        log.info("Updated trainee id={} username={}", updated.getUserId(), updated.getUsername());
        return updated;
    }

    @Transactional
    @Override
    public void deleteTrainee(String traineeId) {
        traineeRepository.delete(traineeId);
        log.info("Permanently deleted trainee id={}", traineeId);
    }

    @Override
    public Trainee getTraineeById(String traineeId) {
        return traineeRepository.findById(traineeId).orElseThrow(() ->
                new ResourceNotFoundException("Trainee", traineeId)
        );
    }

    @Override
    public Trainee getTraineeByUserName(String username) {
        return traineeRepository.findByUserName(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Trainee not found username=" + username)
                );
    }

    @Transactional
    @Override
    public List<Trainer> updateTraineeTrainers(String traineeUsername, @NotEmpty List<Trainer> trainers) {
        Trainee trainee = traineeRepository.findByUserName(traineeUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee not found username=" + traineeUsername));

        trainee.setTrainers(new HashSet<>(trainers));
        Trainee saved = traineeRepository.save(trainee);
        log.info("Updated trainers for trainee username={} trainerCount={}", traineeUsername, trainers.size());

        return new ArrayList<>(saved.getTrainers());
    }
}
