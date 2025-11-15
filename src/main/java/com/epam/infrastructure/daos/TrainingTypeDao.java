package com.epam.infrastructure.daos;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "training_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
@NamedQuery(
        name = "TrainingTypeDao.findByType",
        query = "SELECT t FROM TrainingTypeDao t WHERE t.trainingType = :trainingType"
)
public class TrainingTypeDao {

    @Id
    @GeneratedValue
    @Column(name = "training_type_id")
    private UUID trainingTypeId;

    @Column(name = "training_type", nullable = false, unique = true, length = 50)
    private String trainingType;
}
