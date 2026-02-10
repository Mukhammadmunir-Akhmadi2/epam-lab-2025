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
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Log4j2
@Service
@Validated
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {
    private final TrainerRepository trainerRepository;
    private final BaseUserRepository baseUserRepository;
    private final UsernameGenerator usernameGenerator;
    private final PasswordGenerator passwordGenerator;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    @Override
    public Trainer createTrainer(@Valid Trainer trainer) {
        String username = usernameGenerator.generateUsername(
                trainer, name -> baseUserRepository.findByUsername(name).isPresent()
        );
        String generatedPassword = passwordGenerator.generatePassword(10);

        trainer.setUsername(username);
        trainer.setPassword(passwordEncoder.encode(generatedPassword));
        trainer.setIsActive(true);

        Trainer saved = trainerRepository.save(trainer);

        log.info("Created trainer id={} username={}", saved.getUserId(), saved.getUsername());

        saved.setPassword(generatedPassword);

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
        existing.setIsActive(trainer.getIsActive());

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
