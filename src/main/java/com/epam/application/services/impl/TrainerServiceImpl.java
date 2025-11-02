package com.epam.application.services.impl;

import com.epam.application.exceptions.UserNotFoundException;
import com.epam.application.generators.PasswordGenerator;
import com.epam.application.generators.UsernameGenerator;
import com.epam.application.repository.TrainerRepository;
import com.epam.application.services.TrainerService;
import com.epam.model.Trainer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {
    private static final Logger log = LoggerFactory.getLogger(TrainerServiceImpl.class);

    private final TrainerRepository trainerRepository;

    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;

    @Override
    public Trainer createTrainer(Trainer trainer) {
        String username = usernameGenerator.generateUsername(
                trainer, name -> trainerRepository.findByUserName(name).isPresent()
        );
        String password = passwordGenerator.generatePassword(10);

        trainer.setUserName(username);
        trainer.setPassword(password);
        trainer.setActive(true);

        Trainer saved = trainerRepository.save(trainer);
        log.info("Created trainer id={} username={}", saved.getUserId(), saved.getUserName());
        return saved;
    }

    @Override
    public Trainer updateTrainer(Trainer trainer) {
        Trainer existing = trainerRepository.findById(trainer.getUserId().toString())
                .orElseThrow(() -> new UserNotFoundException("Trainer not found id=" + trainer.getUserId()));

        boolean nameChanged = !existing.getFirstName().equals(trainer.getFirstName())
                || !existing.getLastName().equals(trainer.getLastName());

        existing.setFirstName(trainer.getFirstName());
        existing.setLastName(trainer.getLastName());
        existing.setSpecialization(trainer.getSpecialization());
        existing.setActive(trainer.isActive());

        if (nameChanged) {
            String newUsername = usernameGenerator.generateUsername(
                    existing, name -> trainerRepository.findByUserName(name).isPresent()
            );
            existing.setUserName(newUsername);
            log.info("Username regenerated for trainer id={} -> {}", existing.getUserId(), newUsername);
        }

        Trainer updated = trainerRepository.save(existing);
        log.info("Updated trainer id={} username={}", updated.getUserId(), updated.getUserName());
        return updated;
    }

    @Override
    public Trainer getTrainerById(String trainerId) {
        return trainerRepository.findById(trainerId)
                .orElseThrow(() -> new UserNotFoundException("Trainer not found id=" + trainerId));
    }

    @Override
    public Trainer getTrainerByUserName(String username) {
        return trainerRepository.findByUserName(username)
                .orElseThrow(() -> new UserNotFoundException("Trainer not found username=" + username));
    }

    @Override
    public List<Trainer> getAllTrainers() {
        List<Trainer> trainers = trainerRepository.findAll();
        return trainers.isEmpty() ? new ArrayList<>() : trainers;
    }
}
