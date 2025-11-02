package com.epam.infrastructure.repository;

import com.epam.application.repository.TrainerRepository;
import com.epam.infrastructure.annotations.MapStorage;
import com.epam.infrastructure.daos.TrainerDao;
import com.epam.infrastructure.mappers.TrainerMapper;
import com.epam.model.Trainer;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@MapStorage(file = "trainer.json")
public class InMemoryTrainerRepository implements TrainerRepository {

    private Map<String, TrainerDao> storage;

    private final TrainerMapper trainerMapper = TrainerMapper.INSTANCE;

    @Override
    public Trainer save(Trainer trainer) {
        if (trainer.getUserId() == null) {
            trainer.setUserId(UUID.randomUUID());
        }

        storage.put(
                trainer.getUserId().toString(),
                trainerMapper.toDao(trainer)
        );

        return trainerMapper.toModel(storage.get(trainer.getUserId().toString()));
    }

    @Override
    public Optional<Trainer> findById(String trainerId) {
        return Optional.ofNullable(
                trainerMapper.toModel(storage.get(trainerId))
        );
    }

    @Override
    public Optional<Trainer> findByUserName(String userName) {
        return storage.values().stream()
                .filter(trainerDao -> trainerDao.getUserName().equals(userName))
                .findFirst()
                .map(trainerMapper::toModel);
    }

    @Override
    public List<Trainer> findAll() {
        return trainerMapper
                .toModelList(new ArrayList<>(storage.values()));
    }
}
