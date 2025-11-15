package com.epam.model;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Training {
    private String trainingId;

    private Trainee trainee;

    private Trainer trainer;

    @NotBlank
    private String trainingName;

    @NotNull
    private TrainingType trainingType;

    @NotNull
    @Future
    private LocalDateTime date;

    @NotNull
    private int duration;
}
