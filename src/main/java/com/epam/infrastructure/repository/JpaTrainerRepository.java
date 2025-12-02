package com.epam.infrastructure.repository;

import com.epam.application.repository.TrainerRepository;
import com.epam.infrastructure.daos.TrainerDao;
import com.epam.infrastructure.mappers.TrainerFullMapper;
import com.epam.infrastructure.mappers.TrainerMapper;
import com.epam.model.Trainer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaTrainerRepository implements TrainerRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private final TrainerMapper trainerMapper;
    private final TrainerFullMapper trainerFullMapper;

    @Transactional
    @Override
    public Trainer save(Trainer trainer) {
        TrainerDao trainerDao = trainerFullMapper.toDao(trainer);
        if (trainerDao.getUserId() == null) {
            entityManager.persist(trainerDao);
            return trainerFullMapper.toModel(trainerDao);
        }
        TrainerDao existing = entityManager.find(TrainerDao.class, trainerDao.getUserId());
        trainerMapper.updateFields(trainerDao, existing);
        return trainerFullMapper.toModel(entityManager.merge(trainerDao));
    }

    @Override
    public Optional<Trainer> findById(String trainerId) {
        try {
            TrainerDao trainerDao = entityManager
                    .createNamedQuery("TrainerDao.findById", TrainerDao.class)
                    .setParameter("userId", UUID.fromString(trainerId))
                    .getSingleResult();
            return Optional.of(trainerFullMapper.toModel(trainerDao));
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Trainer> findByUserName(String userName) {
        try {
            TrainerDao trainerDao = entityManager
                    .createNamedQuery("TrainerDao.findByUserName", TrainerDao.class)
                    .setParameter("username", userName)
                    .getSingleResult();
            return Optional.of(trainerFullMapper.toModel(trainerDao));
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Trainer> findAll() {
        return trainerMapper.toModelList(entityManager
                .createNamedQuery("TrainerDao.findAll", TrainerDao.class)
                .getResultList());
    }
}
