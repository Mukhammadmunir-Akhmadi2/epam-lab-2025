package com.epam.model;

import com.epam.infrastructure.enums.TrainingTypeEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingType {
    private String trainingTypeId;
    @NotBlank
    private TrainingTypeEnum trainingType;
}
