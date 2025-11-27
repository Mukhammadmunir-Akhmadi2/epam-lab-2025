package com.epam.infrastructure.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=true)
@Schema(description = "Data Transfer Object for Trainer Response extending TrainerDto")
public class TrainerResponseDto extends TrainerDto {
    @Schema(
            description = "List of trainees assigned to this trainer",
            implementation = TraineeBriefDto.class
    )
    private List<TraineeBriefDto> trainees;
}
