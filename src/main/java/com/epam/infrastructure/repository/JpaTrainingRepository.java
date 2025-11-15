package com.epam.infrastructure.repository;

import com.epam.application.repository.TrainingRepository;
import com.epam.infrastructure.daos.TrainingDao;
import com.epam.infrastructure.mappers.TrainingMapper;
import com.epam.model.Training;
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
public class JpaTrainingRepository implements TrainingRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private final TrainingMapper trainingMapper;

    @Transactional
    @Override
    public Training save(Training training) {
        TrainingDao dao = trainingMapper.toFullDao(training);

        if (dao.getTrainingId() == null) {
            entityManager.persist(dao);
            return trainingMapper.toModel(dao);
        }
        TrainingDao existing = entityManager.find(TrainingDao.class, dao.getTrainingId());
        trainingMapper.updateFields(dao, existing);
        return trainingMapper.toModel(entityManager.merge(existing));
    }

    @Override
    public Optional<Training> findById(String trainingId) {
        try {
            TrainingDao dao = entityManager.createNamedQuery("TrainingDao.findByIdDetailed", TrainingDao.class)
                    .setParameter("trainingId", UUID.fromString(trainingId))
                    .getSingleResult();
            return Optional.of(trainingMapper.toFullModel(dao));
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Training> findAll() {
        List<TrainingDao> daos = entityManager
                .createNamedQuery("TrainingDao.findAllTrainings", TrainingDao.class)
                .getResultList();
        return trainingMapper.toModelList(daos);
    }
}
