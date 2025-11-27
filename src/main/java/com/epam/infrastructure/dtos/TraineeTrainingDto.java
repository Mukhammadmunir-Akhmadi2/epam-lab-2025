package com.epam.infrastructure.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@Schema(description = "Data Transfer Object for Trainer Training extending TrainingDto")
public class TraineeTrainingDto extends TrainingDto {
    @Schema(description = "Username of the trainee", example = "Jane.Smith")
    private String traineeUsername;
}
