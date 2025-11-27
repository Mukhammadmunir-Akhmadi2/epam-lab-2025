package com.epam.application.services.impl;

import com.epam.application.repository.TrainingTypeRepository;
import com.epam.application.services.TrainingTypeService;
import com.epam.infrastructure.enums.TrainingTypeEnum;
import com.epam.model.TrainingType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private final TrainingTypeRepository trainingTypeRepository;

    @Transactional
    @Override
    public TrainingType getTrainingType(TrainingTypeEnum type) {
        return trainingTypeRepository.findByType(type).get();
    }

    @Override
    public List<TrainingType> getAllTrainingTypes() {
        return trainingTypeRepository.findAll();
    }
}