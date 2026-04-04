package com.epam.infrastructure.repository;

import com.epam.application.repository.TrainingRepository;
import com.epam.infrastructure.annotations.MapStorage;
import com.epam.infrastructure.daos.TrainingDao;
import com.epam.infrastructure.mappers.TrainingMapper;
import com.epam.model.Training;
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
@MapStorage(file = "training.json")
@RequiredArgsConstructor
@Profile("dev")
public class InMemoryTrainingRepository implements TrainingRepository {

    private final Map<String, TrainingDao> storage = new HashMap<>();

    private final TrainingMapper trainingMapper;

    @Override
    public Training save(Training training) {
        if (training.getTrainingId() == null) {
            training.setTrainingId(UUID.randomUUID().toString());
        }

        storage.put(
                training.getTrainingId(),
                trainingMapper.toFullDao(training)
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

    @Override
    public List<Training> findByTraineeUsername(String username) {
        return storage.values().stream()
                .filter(t -> t.getTrainee() != null &&
                        t.getTrainee().getUsername().equals(username))
                .map(trainingMapper::toFullModel)
                .toList();
    }

}