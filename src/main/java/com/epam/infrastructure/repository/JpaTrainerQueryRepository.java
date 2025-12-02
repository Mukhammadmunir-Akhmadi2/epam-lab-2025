package com.epam.infrastructure.repository;

import com.epam.application.repository.TrainerQueryRepository;
import com.epam.infrastructure.daos.TraineeDao;
import com.epam.infrastructure.daos.TrainerDao;
import com.epam.infrastructure.mappers.TrainerMapper;
import com.epam.model.Trainer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaTrainerQueryRepository implements TrainerQueryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private final TrainerMapper trainerMapper;

    @Override
    public List<Trainer> findUnassignedActiveTrainersByTraineeUsername(String traineeUsername) {
        TraineeDao traineeDao;
        try {
            traineeDao = entityManager.createNamedQuery("TraineeDao.findByUserName", TraineeDao.class)
                    .setParameter("username", traineeUsername)
                    .getSingleResult();

        } catch (NoResultException e) {
            return List.of();
        }

        List<TrainerDao> trainers = entityManager
                .createQuery(
                        "SELECT t FROM TrainerDao t " +
                        "WHERE :trainee NOT MEMBER OF t.trainees AND t.isActive = true",
                        TrainerDao.class
                )
                .setParameter("trainee", traineeDao)
                .getResultList();
        return trainerMapper.toModelList(trainers);
    }


}
