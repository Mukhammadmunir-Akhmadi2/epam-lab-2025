package com.epam.model;

import com.epam.infrastructure.enums.TrainingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Training {
    private String trainingId;
    private Set<String> traineeIds = new HashSet<>();
    private String trainerId;
    private String trainingName;
    private TrainingType trainingType;
    private LocalDateTime date;
    private String duration;
}
