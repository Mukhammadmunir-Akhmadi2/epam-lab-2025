package com.epam.application.services.impl;

import com.epam.application.repository.TrainingTypeRepository;
import com.epam.application.services.TrainingTypeService;
import com.epam.infrastructure.utils.TrainingTypeUtils;
import com.epam.model.TrainingType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TrainingTypeServiceImpl implements TrainingTypeService {
    private static final Logger logger = LoggerFactory.getLogger(TrainingTypeServiceImpl.class);

    private final TrainingTypeRepository trainingTypeRepository;

    @Transactional
    @Override
    public TrainingType findOrCreate(String typeName) {
        String normalizedTypeName = TrainingTypeUtils.normalize(typeName);
        return trainingTypeRepository.findByType(normalizedTypeName)
                .orElseGet(() -> {
                    TrainingType trainingType = new TrainingType();
                    trainingType.setTrainingType(normalizedTypeName);
                    logger.info("Created new training type: {}", normalizedTypeName);
                    return trainingTypeRepository.save(trainingType);
                });
    }
}