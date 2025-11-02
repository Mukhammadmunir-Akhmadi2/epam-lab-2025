package com.epam.application.services.impl;

import com.epam.application.exceptions.UserNotFoundException;
import com.epam.application.generators.PasswordGenerator;
import com.epam.application.generators.UsernameGenerator;
import com.epam.application.repository.TraineeRepository;
import com.epam.application.services.TraineeService;
import com.epam.model.Trainee;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {
    private static final Logger log = LoggerFactory.getLogger(TraineeServiceImpl.class);

    private final TraineeRepository traineeRepository;

    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;

    @Override
    public Trainee createTrainee(Trainee trainee) {
        String username = usernameGenerator
                .generateUsername(trainee, name -> traineeRepository.findByUserName(name).isPresent());
        String password = passwordGenerator.generatePassword(10);

        trainee.setUserName(username);
        trainee.setPassword(password);
        trainee.setActive(true);

        Trainee saved = traineeRepository.save(trainee);
        log.info("Created trainee id={} username={}", saved.getUserId(), saved.getUserName());
        System.out.println("Created trainee with username: " + username + " and password: " + password);
        return saved;
    }

    @Override
    public Trainee updateTrainee(Trainee trainee) {
        Trainee existing = traineeRepository.findById(trainee.getUserId().toString())
                .orElseThrow(() -> new UserNotFoundException("Trainee not found id=" + trainee.getUserId()));

        boolean nameChanged = !existing.getFirstName().equals(trainee.getFirstName())
                || !existing.getLastName().equals(trainee.getLastName());

        existing.setFirstName(trainee.getFirstName());
        existing.setLastName(trainee.getLastName());

        if (nameChanged) {
            String newUsername = usernameGenerator.generateUsername(
                    existing,
                    name -> traineeRepository.findByUserName(name).isPresent()
            );
            existing.setUserName(newUsername);
            log.info("Username regenerated for trainee id={} -> {}", existing.getUserId(), newUsername);
            System.out.println("Username regenerated for trainee id=" + existing.getUserId() + " -> " + newUsername);
        }

        existing.setDateOfBirth(trainee.getDateOfBirth());
        existing.setAddress(trainee.getAddress());
        existing.setActive(trainee.isActive());

        Trainee updated = traineeRepository.save(existing);

        log.info("Updated trainee id={} username={}", updated.getUserId(), updated.getUserName());
        return updated;
    }

    @Override
    public void deleteTrainee(String traineeId) {
        Trainee trainee = traineeRepository.findById(traineeId)
                .orElseThrow(() -> new UserNotFoundException("Trainee not found id=" + traineeId));
        if (trainee.isActive()) {
            trainee.setActive(false);
            log.info("Soft deleted trainee id={}", traineeId);
            System.out.println("Soft deleted trainee id=" + traineeId);
        } else {
            traineeRepository.delete(traineeId);
            log.info("Permanently deleted trainee id={}", traineeId);
            System.out.println("Permanently deleted trainee id=" + traineeId);
            return;
        }

        traineeRepository.save(trainee);
    }

    @Override
    public Trainee getTraineeById(String traineeId) {
        return traineeRepository.findById(traineeId).orElseThrow(() ->
                new UserNotFoundException("Trainee not found id=" + traineeId)
        );
    }

    @Override
    public Trainee getTraineeByUserName(String username) {
        return traineeRepository.findByUserName(username)
                .orElseThrow(() ->
                        new UserNotFoundException("Trainee not found username=" + username)
                );
    }

    @Override
    public List<Trainee> getAllTrainees() {
        List<Trainee> trainees = traineeRepository.findAll();
        return trainees.isEmpty() ? new ArrayList<>() : trainees;
    }
}
