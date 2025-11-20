package com.epam.infrastructure.daos;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "trainers")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NamedQueries({
        @NamedQuery(
                name = "TrainerDao.findByUserName",
                query = "SELECT t FROM TrainerDao t JOIN FETCH t.specialization WHERE t.userName = :username"
        ),
        @NamedQuery(
                name = "TrainerDao.findAll",
                query = "SELECT t FROM TrainerDao t JOIN FETCH t.specialization"
        )
})
public class TrainerDao extends UserDao {

    @ManyToOne(optional = false)
    @JoinColumn(name = "training_type_id")
    private TrainingTypeDao specialization;

    @EqualsAndHashCode.Exclude
    @ManyToMany(mappedBy = "trainers", fetch = FetchType.LAZY)
    private Set<TraineeDao> trainees = new HashSet<>();
}
