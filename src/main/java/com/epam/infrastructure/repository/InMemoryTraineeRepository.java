package com.epam.infrastructure.repository;

import com.epam.application.repository.TraineeRepository;
import com.epam.infrastructure.annotations.MapStorage;
import com.epam.infrastructure.daos.TraineeDao;
import com.epam.infrastructure.mappers.TraineeFullMapper;
import com.epam.infrastructure.mappers.TraineeMapper;
import com.epam.model.Trainee;
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
@MapStorage(file = "trainee.json")
@RequiredArgsConstructor
@Profile("dev")
public class InMemoryTraineeRepository implements TraineeRepository {

    private final Map<String, TraineeDao> storage = new HashMap<>();

    private final TraineeMapper traineeMapper;
    private final TraineeFullMapper traineeFullMapper;


    @Override
    public Trainee save(Trainee trainee) {
        if (trainee.getUserId() == null) {
            trainee.setUserId(UUID.randomUUID().toString());
        }
        storage.put(
                trainee.getUserId(),
                traineeFullMapper.toDao(trainee)
        );
        return traineeFullMapper.toModel(storage.get(trainee.getUserId()));
    }

    @Override
    public Optional<Trainee> findById(String traineeId) {
        return Optional.ofNullable(
                traineeFullMapper.toModel(
                        storage.get(traineeId)
                )
        );
    }

    @Override
    public Optional<Trainee> findByUserName(String userName) {
        return storage.values().stream()
                        .filter(traineeDao -> traineeDao.getUsername().equals(userName))
                        .findFirst()
                        .map(traineeFullMapper::toModel);
    }

    @Override
    public List<Trainee> findAll() {
        return traineeMapper
                .toModelList(new ArrayList<>(storage.values()));
    }

    @Override
    public void delete(String traineeId) {
        storage.remove(traineeId);
    }
}
