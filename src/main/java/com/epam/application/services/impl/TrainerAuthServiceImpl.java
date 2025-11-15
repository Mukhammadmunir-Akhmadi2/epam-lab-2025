package com.epam.application.services.impl;

import com.epam.application.exceptions.InvalidCredentialException;
import com.epam.application.exceptions.UserNotFoundException;
import com.epam.application.repository.TrainerRepository;
import com.epam.application.services.TrainerAuthService;
import com.epam.model.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrainerAuthServiceImpl implements TrainerAuthService {
    private static final Logger logger = LoggerFactory.getLogger(TrainerAuthServiceImpl.class);

    private final TrainerRepository trainerRepository;

    public TrainerAuthServiceImpl(@Qualifier("jpaTrainerRepository") TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Transactional
    @Override
    public String toggleActive(String trainerUsername) {
        Trainer trainer = trainerRepository.findByUserName(trainerUsername)
                .orElseThrow(() -> new UserNotFoundException("Trainer", trainerUsername));

        if (trainer.isActive()) {
            trainer.setActive(false);
            logger.info("Deactivated trainer username={}", trainerUsername);
        } else {
            trainer.setActive(true);
            logger.info("Activated trainer username={}", trainerUsername);
        }

        trainerRepository.save(trainer);
        return trainer.isActive() ? "activated" : "deactivated";
    }

    @Transactional
    @Override
    public void changePassword(String trainerUsername, String oldPassword, String newPassword) {
        Trainer trainer = trainerRepository.findByUserName(trainerUsername)
                .orElseThrow(() -> new UserNotFoundException("Trainer not found with username: " + trainerUsername));

        if (!trainer.getPassword().equals(oldPassword)) {
            throw new InvalidCredentialException("Old password is incorrect");
        }

        trainer.setPassword(newPassword);
        trainerRepository.save(trainer);
        logger.info("Password changed for trainee username={}", trainerUsername);
    }
}
