package com.epam.infrastructure.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
@Schema(description = "Data Transfer Object for Trainer Training extending TrainingDto")
public class TrainerTrainingDto extends TrainingDto {
    @Schema(description = "Username of the trainer", example = "Alice.Johnson")
    private String trainerUsername;
}
