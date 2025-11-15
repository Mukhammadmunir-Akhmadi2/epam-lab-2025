package com.epam.infrastructure.daos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "trainings")
@Data
@AllArgsConstructor
@NoArgsConstructor
@NamedQueries({
        @NamedQuery(
                name = "TrainingDao.findAllTrainings",
                query = "SELECT t FROM TrainingDao t JOIN FETCH t.trainingType"
        ),
        @NamedQuery(
                name = "TrainingDao.findByIdDetailed",
                query = "SELECT t FROM TrainingDao t JOIN FETCH t.trainer JOIN FETCH t.trainingType LEFT JOIN FETCH t.trainee WHERE t.trainingId = :trainingId"
        )
})
public class TrainingDao {

    @Id
    @GeneratedValue
    @Column(name = "training_id")
    private UUID trainingId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "trainee_id")
    private TraineeDao trainee;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id", nullable = false)
    private TrainerDao trainer;

    @Column(name = "training_name", nullable = false)
    private String trainingName;

    @ManyToOne(optional = false)
    @JoinColumn(name = "training_type_id", nullable = false)
    private TrainingTypeDao trainingType;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private int duration;
}
