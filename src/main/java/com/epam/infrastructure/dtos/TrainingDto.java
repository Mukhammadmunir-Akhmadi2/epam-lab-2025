package com.epam.infrastructure.dtos;

import com.epam.infrastructure.enums.TrainingTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Data Transfer Object for Training details")
public class TrainingDto {
    @NotBlank
    @Schema(description = "Training name", example = "Morning Cardio")
    private String name;
    @NotBlank
    @Schema(description = "Training date and time", example = "2025-11-25 10:15")
    private String date;
    @NotNull
    @Schema(description = "Training type", example = "CARDIO")
    private TrainingTypeEnum type;
    @NotNull
    @Schema(description = "Training duration in minutes", example = "60")
    private Integer duration;
}
