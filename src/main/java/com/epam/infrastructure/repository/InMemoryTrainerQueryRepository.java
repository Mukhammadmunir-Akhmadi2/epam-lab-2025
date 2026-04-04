package com.epam.infrastructure.repository;

import com.epam.application.repository.TrainerQueryRepository;
import com.epam.infrastructure.annotations.MapStorage;
import com.epam.infrastructure.daos.TrainerDao;
import com.epam.infrastructure.mappers.TrainerMapper;
import com.epam.model.Trainer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@MapStorage(file = "trainer.json")
@RequiredArgsConstructor
@Profile("dev")
public class InMemoryTrainerQueryRepository implements TrainerQueryRepository {

    private final Map<String, TrainerDao> storage = new HashMap<>();
    private final TrainerMapper trainerMapper;

    @Override
    public List<Trainer> findUnassignedActiveTrainersByTraineeUsername(String traineeUsername) {
        return trainerMapper.toModelList(storage.values().stream()
                .filter(TrainerDao::getIsActive)
                .filter(t -> t.getTrainees() == null ||
                        t.getTrainees().stream()
                                .noneMatch(te -> te.getUsername().equals(traineeUsername)))
                .toList());
    }
}