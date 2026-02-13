package com.epam.infrastructure.integration;

import com.epam.application.port.WorkloadEventPublisher;
import com.epam.infrastructure.enums.ActionType;
import com.epam.infrastructure.mappers.TrainerWorkloadEventMapper;
import com.epam.model.Training;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class WorkloadEventPublisherImpl implements WorkloadEventPublisher {

    private final WorkloadServiceClientImpl workloadGateway;
    private final TrainerWorkloadEventMapper mapper;

    @Override
    public void publishTrainingAdded(Training training) {
        log.info(
                "Publishing training ADD event. trainingId={}, trainer={}",
                training.getTrainingId(), training.getTrainer().getUsername()
        );
        workloadGateway.send(mapper.toDto(training, ActionType.ADD));
    }

    @Override
    public void publishTrainingDeleted(Training training) {
        log.info(
                "Publishing training DELETE event. trainingId={}, trainer={}",
                training.getTrainingId(), training.getTrainer().getUsername()
        );
        workloadGateway.send(mapper.toDto(training, ActionType.DELETE));
    }

    @Override
    public void publishTrainingsDeleted(List<Training> trainings) {
        log.info(
                "Publishing bulk training DELETE events. count={}, trainer={}",
                trainings.size(),
                trainings.get(0).getTrainer().getUsername()
        );
        trainings.stream()
                .map(t -> mapper.toDto(t, ActionType.DELETE))
                .forEach(workloadGateway::send);
    }
}