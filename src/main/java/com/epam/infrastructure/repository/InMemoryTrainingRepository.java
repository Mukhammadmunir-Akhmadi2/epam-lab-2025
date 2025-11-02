package com.epam.infrastructure.repository;

import com.epam.application.repository.TrainingRepository;
import com.epam.infrastructure.annotations.MapStorage;
import com.epam.infrastructure.daos.TrainingDao;
import com.epam.infrastructure.mappers.TrainingMapper;
import com.epam.model.Training;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@MapStorage(file = "training.json")
public class InMemoryTrainingRepository implements TrainingRepository {

    private Map<String, TrainingDao> storage;

    private final TrainingMapper trainingMapper = TrainingMapper.INSTANCE;

    @Override
    public Training save(Training training) {
        if (training.getTrainingId() == null) {
            training.setTrainingId(UUID.randomUUID().toString());
        }

        storage.put(
                training.getTrainingId(),
                trainingMapper.toDao(training)
        );

        return trainingMapper.toModel(storage.get(training.getTrainingId()));
    }

    @Override
    public Optional<Training> findById(String trainingId) {
        return Optional.ofNullable(
                trainingMapper.toModel(storage.get(trainingId))
        );
    }

    @Override
    public List<Training> findAll() {
        return trainingMapper
                .toModelList(new ArrayList<>(storage.values()));
    }

}
