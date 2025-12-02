package com.epam.infrastructure.dtos;

import com.epam.infrastructure.enums.TrainingTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Data Transfer Object for Training Type")
public class TrainingTypeDto {
    @Schema(description = "Unique identifier of the training type", example = "11111111-1111-1111-111111111111")
    private String trainingTypeId;
    @Schema(description = "Name of the training type", example = "CARDIO")
    private TrainingTypeEnum trainingType;
}
