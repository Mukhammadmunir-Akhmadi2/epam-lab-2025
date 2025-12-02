package com.epam.application.services;

import com.epam.application.repository.TrainingTypeRepository;
import com.epam.application.services.impl.TrainingTypeServiceImpl;
import com.epam.infrastructure.enums.TrainingTypeEnum;
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
        existingType.setTrainingType(TrainingTypeEnum.CARDIO);
    }

    @Test
    void findOrCreate_shouldReturnExistingType() {
        String typeName = "CARDIO";

        when(trainingTypeRepository.findByType(TrainingTypeEnum.valueOf(typeName)))
                .thenReturn(Optional.of(existingType));

        TrainingType result = trainingTypeService.getTrainingType(TrainingTypeEnum.valueOf(typeName));

        assertNotNull(result);
        assertEquals(TrainingTypeEnum.CARDIO, result.getTrainingType());

        verify(trainingTypeRepository, times(1))
                .findByType(TrainingTypeEnum.valueOf(typeName));
        verify(trainingTypeRepository, never()).save(any());
    }
}