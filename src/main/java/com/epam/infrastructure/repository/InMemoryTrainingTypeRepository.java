package com.epam.infrastructure.repository;

import com.epam.application.repository.TrainingTypeRepository;
import com.epam.infrastructure.annotations.MapStorage;
import com.epam.infrastructure.daos.TrainingTypeDao;
import com.epam.infrastructure.enums.TrainingTypeEnum;
import com.epam.infrastructure.mappers.TrainingTypeMapper;
import com.epam.model.TrainingType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@MapStorage(file = "training-type.json")
@RequiredArgsConstructor
@Profile("dev")
public class InMemoryTrainingTypeRepository implements TrainingTypeRepository {

    private final Map<String, TrainingTypeDao> storage = new HashMap<>();
    private final TrainingTypeMapper trainingTypeMapper;

    @Override
    public Optional<TrainingType> findByType(TrainingTypeEnum type) {
        return storage.values().stream()
                .filter(t -> t.getTrainingType().equals(type))
                .findFirst()
                .map(trainingTypeMapper::toModel);
    }

    @Override
    public TrainingType save(TrainingType trainingType) {
        if (trainingType.getTrainingTypeId() == null) {
            trainingType.setTrainingTypeId(UUID.randomUUID().toString());
        }
        storage.put(trainingType.getTrainingTypeId(), trainingTypeMapper.toDao(trainingType));
        return trainingTypeMapper.toModel(storage.get(trainingType.getTrainingTypeId()));
    }

    @Override
    public List<TrainingType> findAll() {
        return trainingTypeMapper.toModelList(new ArrayList<>(storage.values()));
    }
}