package com.epam.application.services;

import com.epam.application.repository.TrainingTypeRepository;
import com.epam.application.services.impl.TrainingTypeServiceImpl;
import com.epam.infrastructure.utils.TrainingTypeUtils;
import com.epam.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingTypeServiceImplTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainingTypeServiceImpl trainingTypeService;

    private TrainingType existingType;

    @BeforeEach
    void setUp() {
        existingType = new TrainingType();
        existingType.setTrainingType("CARDIO");
    }

    @Test
    void findOrCreate_shouldReturnExistingType() {
        String typeName = "cardio";

        when(trainingTypeRepository.findByType(TrainingTypeUtils.normalize(typeName)))
                .thenReturn(Optional.of(existingType));

        TrainingType result = trainingTypeService.findOrCreate(typeName);

        assertNotNull(result);
        assertEquals("CARDIO", result.getTrainingType());

        verify(trainingTypeRepository, times(1))
                .findByType(TrainingTypeUtils.normalize(typeName));
        verify(trainingTypeRepository, never()).save(any());
    }

    @Test
    void findOrCreate_shouldCreateNewTypeIfNotExists() {
        String typeName = "strength";

        when(trainingTypeRepository.findByType(TrainingTypeUtils.normalize(typeName)))
                .thenReturn(Optional.empty());

        TrainingType savedType = new TrainingType();
        savedType.setTrainingType(TrainingTypeUtils.normalize(typeName));

        when(trainingTypeRepository.save(any())).thenReturn(savedType);

        TrainingType result = trainingTypeService.findOrCreate(typeName);

        assertNotNull(result);
        assertEquals("STRENGTH", result.getTrainingType());

        verify(trainingTypeRepository, times(1))
                .findByType(TrainingTypeUtils.normalize(typeName));
        verify(trainingTypeRepository, times(1)).save(any());
    }
}