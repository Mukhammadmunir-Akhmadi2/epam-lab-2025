package com.epam.infrastructure.repository;

import com.epam.application.repository.TraineeRepository;
import com.epam.infrastructure.annotations.MapStorage;
import com.epam.infrastructure.daos.TraineeDao;
import com.epam.infrastructure.mappers.TraineeMapper;
import com.epam.model.Trainee;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;

@Repository
@MapStorage(file = "trainee.json")
public class InMemoryTraineeRepository implements TraineeRepository {

    private Map<String, TraineeDao> storage;

    private final TraineeMapper traineeMapper = TraineeMapper.INSTANCE;

    @Override
    public Trainee save(Trainee trainee) {
        if (trainee.getUserId() == null) {
            trainee.setUserId(UUID.randomUUID().toString());
        }
        storage.put(
                trainee.getUserId(),
                traineeMapper.toDao(trainee)
        );
        return traineeMapper.toModel(storage.get(trainee.getUserId()));
    }

    @Override
    public Optional<Trainee> findById(String traineeId) {
        return Optional.ofNullable(
                traineeMapper.toModel(
                        storage.get(traineeId)
                )
        );
    }

    @Override
    public Optional<Trainee> findByUserName(String userName) {
        return storage.values().stream()
                        .filter(traineeDao -> traineeDao.getUserName().equals(userName))
                        .findFirst()
                        .map(traineeMapper::toModel);
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
