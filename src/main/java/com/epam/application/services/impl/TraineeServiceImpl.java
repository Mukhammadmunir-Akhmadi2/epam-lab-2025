package com.epam.application.services.impl;

import com.epam.application.exceptions.ResourceNotFoundException;
import com.epam.application.generators.PasswordGenerator;
import com.epam.application.generators.UsernameGenerator;
import com.epam.application.repository.BaseUserRepository;
import com.epam.application.repository.TraineeRepository;
import com.epam.application.repository.TrainingRepository;
import com.epam.application.services.TraineeService;
import com.epam.application.tx.AfterCommitExecutor;
import com.epam.application.port.WorkloadEventPublisher;
import com.epam.model.Trainee;
import com.epam.model.Trainer;
import com.epam.model.Training;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Log4j2
@Service
@Validated
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {
    private final TraineeRepository traineeRepository;
    private final BaseUserRepository baseUserRepository;
    private final TrainingRepository trainingRepository;

    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;
    private final PasswordEncoder passwordEncoder;

    private final AfterCommitExecutor afterCommitExecutor;
    private final WorkloadEventPublisher workloadPublisher;

    @Transactional
    @Override
    public Trainee createTrainee(@Valid Trainee trainee) {
        String username = usernameGenerator
                .generateUsername(trainee, name -> baseUserRepository.findByUsername(name).isPresent());
        String generatedPassword = passwordGenerator.generatePassword(10);

        trainee.setUsername(username);
        trainee.setPassword(passwordEncoder.encode(generatedPassword));
        trainee.setIsActive(true);

        Trainee saved = traineeRepository.save(trainee);

        log.info("Created trainee id={} username={}", saved.getUserId(), saved.getUsername());

        saved.setPassword(generatedPassword);

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
        existing.setIsActive(trainee.getIsActive());

        Trainee updated = traineeRepository.save(existing);

        log.info("Updated trainee id={} username={}", updated.getUserId(), updated.getUsername());
        return updated;
    }

    @Override
    @Transactional
    public void deleteTrainee(String username) {

        Trainee trainee = traineeRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee", username));

        List<Training> trainings = trainingRepository.findByTraineeUsername(username);

        traineeRepository.delete(trainee.getUserId());

        afterCommitExecutor.run(() -> workloadPublisher.publishTrainingsDeleted(trainings));

        log.info("Permanently deleted trainee id={} username={} cascadedTrainings={}",
                username, username, trainings.size());
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
