package com.epam.infrastructure.controllers.Impl;

import com.epam.application.services.TrainingTypeService;
import com.epam.infrastructure.controllers.TrainingTypeController;
import com.epam.infrastructure.dtos.TrainingTypeDto;
import com.epam.infrastructure.mappers.TrainingTypeMapper;
import com.epam.model.TrainingType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TrainingTypeControllerImpl implements TrainingTypeController {

    private final TrainingTypeService trainingTypeService;
    private final TrainingTypeMapper trainingTypeMapper;

    @Override
    public ResponseEntity<List<TrainingTypeDto>> getTrainingTypes() {
        trainingTypeService.getAllTrainingTypes();

        List<TrainingType> trainingTypes = trainingTypeService.getAllTrainingTypes();
        List<TrainingTypeDto> responseDto = trainingTypeMapper.toDtoList(trainingTypes);

        return ResponseEntity.ok().body(responseDto);
    }
}
