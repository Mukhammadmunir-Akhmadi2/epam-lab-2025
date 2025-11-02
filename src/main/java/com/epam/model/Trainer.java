package com.epam.model;

import com.epam.infrastructure.enums.TrainingType;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Trainer extends User {
    private TrainingType specialization;
}
