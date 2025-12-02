package com.epam.infrastructure.dtos;

import com.epam.infrastructure.enums.TrainingTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Brief information about a trainer")
public class TrainerBriefDto {
    @Schema(description = "Username of the trainer", example = "jane.doe")
    private String username;
    @Schema(description = "First name of the trainer", example = "Jane")
    private String firstName;
    @Schema(description = "Last name of the trainer", example = "Doe")
    private String lastName;
    @Schema(
            description = "Trainer specialization (training type reference)",
            example = "STRENGTH"
    )
    private TrainingTypeEnum specialization;
}
