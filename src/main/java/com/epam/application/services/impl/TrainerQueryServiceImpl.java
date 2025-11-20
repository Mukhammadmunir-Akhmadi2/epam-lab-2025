package com.epam.application.services.impl;

import com.epam.application.repository.TrainerQueryRepository;
import com.epam.application.services.TrainerQueryService;
import com.epam.model.Trainer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainerQueryServiceImpl implements TrainerQueryService {

    private final TrainerQueryRepository trainerQueryRepository;

    @Override
    public List<Trainer> getUnassignedTrainers(String traineeUsername) {
        return trainerQueryRepository.findUnassignedTrainersByTraineeUsername(traineeUsername);
    }
}
