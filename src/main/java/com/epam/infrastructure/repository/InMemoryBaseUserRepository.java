package com.epam.infrastructure.repository;

import com.epam.application.repository.BaseUserRepository;
import com.epam.model.Trainee;
import com.epam.model.Trainer;
import com.epam.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Profile("dev")
@RequiredArgsConstructor
public class InMemoryBaseUserRepository implements BaseUserRepository {

    private final InMemoryTraineeRepository traineeRepo;
    private final InMemoryTrainerRepository trainerRepo;

    @Override
    public Optional<User> findByUsername(String username) {
        Optional<Trainee> trainee = traineeRepo.findByUserName(username);
        if (trainee.isPresent()) return Optional.of(trainee.get());

        return trainerRepo.findByUserName(username)
                .map(t -> (User) t);
    }

    @Override
    public User save(User user) {
        Trainee trainee = traineeRepo.findById(user.getUserId()).orElse(null);
        Trainer trainer = trainerRepo.findById(user.getUserId()).orElse(null);
        if (trainee != null) {
            trainee.setPassword(user.getPassword());
            trainee.setIsActive(user.getIsActive());
            return traineeRepo.save((Trainee) user);
        }

        if (trainer != null) {
            trainer.setPassword(user.getPassword());
            trainer.setIsActive(user.getIsActive());
            return trainerRepo.save((Trainer) user);
        }

        throw new UnsupportedOperationException(
                "In dev profile, save user via TraineeRepository or TrainerRepository"
        );
    }
}