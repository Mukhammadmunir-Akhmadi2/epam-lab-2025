package com.epam.application.services.impl;

import com.epam.application.exceptions.UserNotFoundException;
import com.epam.application.generators.PasswordGenerator;
import com.epam.application.generators.UsernameGenerator;
import com.epam.application.repository.TraineeRepository;
import com.epam.application.services.TraineeService;
import com.epam.model.Trainee;
import com.epam.model.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
public class TraineeServiceImpl implements TraineeService {
    private static final Logger log = LoggerFactory.getLogger(TraineeServiceImpl.class);

    private final TraineeRepository traineeRepository;

    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;

    public TraineeServiceImpl( @Qualifier("jpaTraineeRepository") TraineeRepository traineeRepository, UsernameGenerator usernameGenerator, PasswordGenerator passwordGenerator) {
        this.traineeRepository = traineeRepository;
        this.usernameGenerator = usernameGenerator;
        this.passwordGenerator = passwordGenerator;
    }

    @Transactional
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
        return saved;
    }

    @Transactional
    @Override
    public Trainee updateTrainee(Trainee trainee) {
        Trainee existing = traineeRepository.findById(trainee.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Trainee", trainee.getUserId()));

        boolean isNameChanged = !existing.getFirstName().equals(trainee.getFirstName())
                || !existing.getLastName().equals(trainee.getLastName());

        existing.setFirstName(trainee.getFirstName());
        existing.setLastName(trainee.getLastName());

        if (isNameChanged) {
            String newUsername = usernameGenerator.generateUsername(
                    existing,
                    name -> traineeRepository.findByUserName(name).isPresent()
            );
            existing.setUserName(newUsername);
            log.info("Username regenerated for trainee id={} -> {}", existing.getUserId(), newUsername);
        }

        existing.setDateOfBirth(trainee.getDateOfBirth());
        existing.setAddress(trainee.getAddress());
        existing.setActive(trainee.isActive());

        Trainee updated = traineeRepository.save(existing);

        log.info("Updated trainee id={} username={}", updated.getUserId(), updated.getUserName());
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
                new UserNotFoundException("Trainee", traineeId)
        );
    }

    @Override
    public Trainee getTraineeByUserName(String username) {
        return traineeRepository.findByUserName(username)
                .orElseThrow(() ->
                        new UserNotFoundException("Trainee not found username=" + username)
                );
    }

    @Transactional
    @Override
    public void updateTraineeTrainers(String traineeUsername, List<Trainer> trainers) {
        Trainee trainee = traineeRepository.findByUserName(traineeUsername)
                .orElseThrow(() -> new UserNotFoundException("Trainee", traineeUsername));

        trainee.setTrainers(new HashSet<>(trainers));
        traineeRepository.save(trainee);
    }
}
