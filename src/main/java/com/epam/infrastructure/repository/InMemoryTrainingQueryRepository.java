package com.epam.infrastructure.repository;

import com.epam.application.repository.TrainingQueryRepository;
import com.epam.infrastructure.annotations.MapStorage;
import com.epam.infrastructure.daos.TrainingDao;
import com.epam.infrastructure.enums.TrainingTypeEnum;
import com.epam.infrastructure.mappers.TrainingMapper;
import com.epam.model.Training;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@MapStorage(file = "training.json")
@RequiredArgsConstructor
@Profile("dev")
public class InMemoryTrainingQueryRepository implements TrainingQueryRepository {

    private final Map<String, TrainingDao> storage = new HashMap<>();
    private final TrainingMapper trainingMapper;

    @Override
    public List<Training> findTrainingsByTraineeUsernameWithFilters(
            String traineeUsername, LocalDate from, LocalDate to,
            String trainerName, TrainingTypeEnum trainingType) {

        return storage.values().stream()
                .filter(t -> t.getTrainee().getUsername().equals(traineeUsername))
                .filter(t -> from == null || !t.getDate().isBefore(from.atStartOfDay()))
                .filter(t -> to == null || !t.getDate().isAfter(to.atTime(23, 59, 59)))
                .filter(t -> trainerName == null || t.getTrainer().getUsername().contains(trainerName))
                .filter(t -> trainingType == null || t.getTrainingType().getTrainingType().equals(trainingType))
                .map(trainingMapper::toFullModel)
                .toList();
    }

    @Override
    public List<Training> findTrainingsByTrainerUsernameWithFilters(
            String trainerUsername, LocalDate from, LocalDate to, String traineeName) {

        return storage.values().stream()
                .filter(t -> t.getTrainer().getUsername().equals(trainerUsername))
                .filter(t -> from == null || !t.getDate().isBefore(from.atStartOfDay()))
                .filter(t -> to == null || !t.getDate().isAfter(to.atTime(23, 59, 59)))
                .filter(t -> traineeName == null || t.getTrainee().getUsername().contains(traineeName))
                .map(trainingMapper::toFullModel)
                .toList();
    }

    @Override
    public boolean existsConflictForTrainer(String trainerUsername,
                                            LocalDateTime newStart, LocalDateTime newEnd) {

        return storage.values().stream()
                .filter(t -> t.getTrainer().getUsername().equals(trainerUsername))
                .anyMatch(t -> {
                    LocalDateTime existingStart = t.getDate();
                    LocalDateTime existingEnd = existingStart.plusMinutes(t.getDuration());
                    return existingStart.isBefore(newEnd) && existingEnd.isAfter(newStart);
                });
    }
}