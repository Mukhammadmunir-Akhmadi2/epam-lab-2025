package com.epam.infrastructure.repository;

import com.epam.application.repository.TrainerRepository;
import com.epam.infrastructure.annotations.MapStorage;
import com.epam.infrastructure.daos.TrainerDao;
import com.epam.infrastructure.mappers.TrainerFullMapper;
import com.epam.infrastructure.mappers.TrainerMapper;
import com.epam.model.Trainer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;

@Repository
@MapStorage(file = "trainer.json")
@RequiredArgsConstructor
@Profile("dev")
public class InMemoryTrainerRepository implements TrainerRepository {

    private final Map<String, TrainerDao> storage = new HashMap<>();

    private final TrainerMapper trainerMapper;
    private final TrainerFullMapper trainerFullMapper;


    @Override
    public Trainer save(Trainer trainer) {
        if (trainer.getUserId() == null) {
            trainer.setUserId(UUID.randomUUID().toString());
        }

        storage.put(
                trainer.getUserId(),
                trainerFullMapper.toDao(trainer)
        );

        return trainerFullMapper.toModel(storage.get(trainer.getUserId()));
    }

    @Override
    public Optional<Trainer> findById(String trainerId) {
        return Optional.ofNullable(
                trainerFullMapper.toModel(storage.get(trainerId))
        );
    }

    @Override
    public Optional<Trainer> findByUserName(String userName) {
        return storage.values().stream()
                .filter(trainerDao -> trainerDao.getUsername().equals(userName))
                .findFirst()
                .map(trainerFullMapper::toModel);
    }

    @Override
    public List<Trainer> findAll() {
        return trainerMapper
                .toModelList(new ArrayList<>(storage.values()));
    }
}
