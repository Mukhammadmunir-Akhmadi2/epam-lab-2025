package com.epam.application.services.impl;

import com.epam.application.exceptions.TrainingNotFoundException;
import com.epam.application.repository.TrainingRepository;
import com.epam.application.services.TrainingService;
import com.epam.model.Training;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {
    private static final Logger log = LoggerFactory.getLogger(TrainingServiceImpl.class);

    private final TrainingRepository trainingRepository;

    @Override
    public Training createTraining(Training training) {
        Training saved = trainingRepository.save(training);
        log.info("Created training id={} name={}", saved.getTrainingId(), saved.getTrainingName());
        return saved;
    }

    @Override
    public Training getTrainingById(String trainingId) {
        return trainingRepository.findById(trainingId)
                .orElseThrow(() -> new TrainingNotFoundException("Training not found id=" + trainingId));
    }

    @Override
    public List<Training> getAllTrainings() {
        List<Training> trainings = trainingRepository.findAll();
        return trainings.isEmpty() ? new ArrayList<>() : trainings;
    }
}
