package com.epam.application.services.impl;

import com.epam.application.exceptions.InvalidCredentialException;
import com.epam.application.exceptions.UserNotFoundException;
import com.epam.application.repository.TraineeRepository;
import com.epam.application.services.TraineeAuthService;
import com.epam.model.Trainee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TraineeAuthServiceImpl implements TraineeAuthService {
    private static final Logger logger = LoggerFactory.getLogger(TraineeAuthServiceImpl.class);

    private final TraineeRepository traineeRepository;

    public TraineeAuthServiceImpl(@Qualifier("jpaTraineeRepository") TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    @Transactional
    @Override
    public String toggleActive(String traineeUsername) {
        Trainee trainee = traineeRepository.findByUserName(traineeUsername)
                .orElseThrow(() -> new UserNotFoundException("Trainer not found with username: " + traineeUsername));

        if (trainee.isActive()) {
            trainee.setActive(false);
            logger.info("Deactivated trainer username={}", traineeUsername);
        } else {
            trainee.setActive(true);
            logger.info("Activated trainer username={}", traineeUsername);
        }

        traineeRepository.save(trainee);
        return trainee.isActive() ? "activated" : "deactivated";
    }

    @Transactional
    @Override
    public void changePassword(String traineeUsername, String oldPassword, String newPassword) {
        Trainee trainee = traineeRepository.findByUserName(traineeUsername)
                .orElseThrow(() -> new UserNotFoundException("Trainee not found with username: " + traineeUsername));

        if (!trainee.getPassword().equals(oldPassword)) {
            throw new InvalidCredentialException("Old password is incorrect");
        }

        trainee.setPassword(newPassword);
        traineeRepository.save(trainee);
        logger.info("Password changed for trainee username={}", traineeUsername);
    }
}
