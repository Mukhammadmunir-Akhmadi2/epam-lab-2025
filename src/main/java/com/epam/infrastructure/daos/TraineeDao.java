package com.epam.infrastructure.daos;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "trainees")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NamedQueries({
        @NamedQuery(
                name = "TraineeDao.findByUserName",
                query = "SELECT t FROM TraineeDao t LEFT JOIN FETCH t.trainers WHERE t.username = :username"
        ),
        @NamedQuery(
                name = "TraineeDao.findAll",
                query = "SELECT t FROM TraineeDao t"
        ),
        @NamedQuery(
                name = "TraineeDao.findById",
                query = "SELECT t FROM TraineeDao t LEFT JOIN FETCH t.trainers WHERE t.userId = :userId"
        )
})
public class TraineeDao extends UserDao {

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "address")
    private String address;

    @EqualsAndHashCode.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "trainee_trainer",
            joinColumns = @JoinColumn(name = "trainee_id"),
            inverseJoinColumns = @JoinColumn(name = "trainer_id"))
    private Set<TrainerDao> trainers = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "trainee", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TrainingDao> trainings = new HashSet<>();

}
