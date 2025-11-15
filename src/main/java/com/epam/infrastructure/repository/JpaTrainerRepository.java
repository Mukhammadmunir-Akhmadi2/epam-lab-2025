package com.epam.infrastructure.repository;

import com.epam.application.repository.TrainerRepository;
import com.epam.infrastructure.daos.TrainerDao;
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

    @Transactional
    @Override
    public Trainer save(Trainer trainer) {
        TrainerDao trainerDao = trainerMapper.toDao(trainer);
        if (trainerDao.getUserId() == null) {
            entityManager.persist(trainerDao);
            return trainerMapper.toModel(trainerDao);
        }
        TrainerDao existing = entityManager.find(TrainerDao.class, trainerDao.getUserId());
        trainerMapper.updateFields(trainerDao, existing);
        return trainerMapper.toModel(entityManager.merge(trainerDao));
    }

    @Override
    public Optional<Trainer> findById(String trainerId) {
        return Optional.ofNullable(
                trainerMapper.toModel(entityManager.find(TrainerDao.class, UUID.fromString(trainerId)))
        );
    }

    @Override
    public Optional<Trainer> findByUserName(String userName) {
        try {
            TrainerDao trainerDao = entityManager
                    .createNamedQuery("TrainerDao.findByUserName", TrainerDao.class)
                    .setParameter("username", userName)
                    .getSingleResult();
            return Optional.of(trainerMapper.toModel(trainerDao));
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
