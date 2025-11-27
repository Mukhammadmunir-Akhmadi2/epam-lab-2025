package com.epam.infrastructure.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=true)
@Schema(description = "Data Transfer Object for Trainee Response extending TraineeDto")
public class TraineeResponseDto extends TraineeDto {
    @Schema(description = "List of assigned trainers")
    private List<TrainerBriefDto> trainers;
}
