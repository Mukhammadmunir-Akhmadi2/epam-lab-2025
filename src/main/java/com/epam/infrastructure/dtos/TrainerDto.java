package com.epam.infrastructure.dtos;

import com.epam.infrastructure.enums.TrainingTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Trainer profile data transfer object")
public class TrainerDto {
    @NotBlank
    @Schema(description = "Trainer username", example = "john.doe")
    private String username;
    @NotBlank
    @Schema(description = "Trainer first name", example = "John")
    private String firstName;
    @NotBlank
    @Schema(description = "Trainer last name", example = "Doe")
    private String lastName;
    @NotNull
    @Schema(
            description = "Trainer specialization (training type reference)",
            example = "CARDIO"
    )
    private TrainingTypeEnum specialization;
    @NotNull
    @Schema(description = "Trainer active status", example = "true")
    private boolean isActive;
}
