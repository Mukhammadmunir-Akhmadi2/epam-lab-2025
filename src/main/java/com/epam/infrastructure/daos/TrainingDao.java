package com.epam.infrastructure.daos;

import com.epam.infrastructure.enums.TrainingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingDao {
    private UUID trainingId;
    private Set<String> traineeIds = new HashSet<>();
    private String trainerId;
    private String trainingName;
    private TrainingType trainingType;
    private String date;
    private String duration;
}
