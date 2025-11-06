package com.epam.infrastructure.daos;

import com.epam.infrastructure.enums.TrainingType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TrainerDao extends UserDao {
    private TrainingType specialization;

}
