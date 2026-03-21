package com.epam.application.services.impl;

import com.epam.application.exceptions.ResourceNotFoundException;
import com.epam.application.exceptions.TrainerScheduleConflictException;
import com.epam.application.repository.TrainingRepository;
import com.epam.application.services.TrainingService;
import com.epam.application.tx.AfterCommitExecutor;
import com.epam.application.port.WorkloadEventPublisher;
import com.epam.model.Training;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@Validated
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final AfterCommitExecutor afterCommitExecutor;
    private final WorkloadEventPublisher workloadPublisher;

    private final TrainingQueryServiceImpl trainingQueryService;

    @Transactional
    @Override
    public Training createTraining(@Valid Training training) {
        training.setIsActive(true);

        if (trainingQueryService
                .hasTrainerConflict(
                        training.getTrainer().getUsername(),
                        training.getDate(),
                        training.getDuration()))
        {
            throw new TrainerScheduleConflictException(
                    "Trainer already has a training at this time"
            );
        }

        Training saved = trainingRepository.save(training);

        Training db = trainingRepository.findById(saved.getTrainingId()).get();

        afterCommitExecutor.run(() -> workloadPublisher.publishTrainingAdded(db));

        log.info("Created training id={} name={}", saved.getTrainingId(), saved.getTrainingName());
        return saved;
    }

    @Override
    public Training getTrainingById(String trainingId) {
        return trainingRepository.findById(trainingId)
                .orElseThrow(() -> new ResourceNotFoundException("Training not found id=" + trainingId));
    }

    @Override
    public List<Training> getAllTrainings() {
        List<Training> trainings = trainingRepository.findAll();
        return trainings.isEmpty() ? new ArrayList<>() : trainings;
    }
}
