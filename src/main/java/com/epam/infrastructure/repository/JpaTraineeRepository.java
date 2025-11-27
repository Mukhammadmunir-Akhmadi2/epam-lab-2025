package com.epam.infrastructure.repository;

import com.epam.application.repository.TraineeRepository;
import com.epam.infrastructure.daos.TraineeDao;
import com.epam.infrastructure.mappers.TraineeFullMapper;
import com.epam.infrastructure.mappers.TraineeMapper;
import com.epam.model.Trainee;
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
public class JpaTraineeRepository implements TraineeRepository {

    private final TraineeMapper traineeMapper;
    private final TraineeFullMapper traineeFullMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public Trainee save(Trainee trainee) {
        TraineeDao traineeDao = traineeFullMapper.toDao(trainee);
        if (traineeDao.getUserId() == null) {
            entityManager.persist(traineeDao);
            return traineeFullMapper.toModel(traineeDao);
        }
        TraineeDao existing = entityManager.find(TraineeDao.class, traineeDao.getUserId());
        traineeMapper.updateFields(traineeDao, existing);
        return traineeFullMapper.toModel(entityManager.merge(existing));
    }

    @Override
    public Optional<Trainee> findById(String traineeId) {
        try {
            TraineeDao traineeDao = entityManager
                    .createNamedQuery("TraineeDao.findById", TraineeDao.class)
                    .setParameter("userId", UUID.fromString(traineeId))
                    .getSingleResult();
            return Optional.ofNullable(traineeFullMapper.toModel(traineeDao));
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Trainee> findByUserName(String userName) {
        try {
            TraineeDao traineeDao = entityManager
                    .createNamedQuery("TraineeDao.findByUserName", TraineeDao.class)
                    .setParameter("username", userName)
                    .getSingleResult();
            return Optional.ofNullable(traineeFullMapper.toModel(traineeDao));
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Trainee> findAll() {
        return traineeMapper
                .toModelList(
                        entityManager
                                .createNamedQuery("TraineeDao.findAll", TraineeDao.class)
                                .getResultList()
                );
    }

    @Transactional
    @Override
    public void delete(String traineeId) {
        TraineeDao trainee = entityManager.find(TraineeDao.class, UUID.fromString(traineeId));
        if (trainee != null) {
            entityManager.remove(trainee);
        }
    }
}
