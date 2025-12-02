package com.epam.application.services.impl;

import com.epam.application.exceptions.ResourceNotFoundException;
import com.epam.application.generators.PasswordGenerator;
import com.epam.application.generators.UsernameGenerator;
import com.epam.application.repository.BaseUserRepository;
import com.epam.application.repository.TrainerRepository;
import com.epam.application.services.TrainerService;
import com.epam.model.Trainer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {
    private static final Logger log = LoggerFactory.getLogger(TrainerServiceImpl.class);

    private final TrainerRepository trainerRepository;
    private final BaseUserRepository baseUserRepository;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;


    @Transactional
    @Override
    public Trainer createTrainer(@Valid Trainer trainer) {
        String username = usernameGenerator.generateUsername(
                trainer, name -> baseUserRepository.findByUserName(name).isPresent()
        );
        String password = passwordGenerator.generatePassword(10);

        trainer.setUsername(username);
        trainer.setPassword(password);
        trainer.setActive(true);

        Trainer saved = trainerRepository.save(trainer);
        log.info("Created trainer id={} username={}", saved.getUserId(), saved.getUsername());
        return saved;
    }

    @Transactional
    @Override
    public Trainer updateTrainer(@Valid Trainer trainer) {
        Trainer existing = trainerRepository.findByUserName(trainer.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found username=" + trainer.getUsername()));

        existing.setFirstName(trainer.getFirstName());
        existing.setLastName(trainer.getLastName());
        existing.setSpecialization(trainer.getSpecialization());
        existing.setActive(trainer.isActive());

        Trainer updated = trainerRepository.save(existing);
        log.info("Updated trainer id={} username={}", updated.getUserId(), updated.getUsername());
        return updated;
    }

    @Override
    public Trainer getTrainerById(String trainerId) {
        return trainerRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer", trainerId));
    }

    @Override
    public Trainer getTrainerByUserName(String trainerUserName) {
        return trainerRepository.findByUserName(trainerUserName)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found username=" + trainerUserName));
    }
}
