package com.epam.infrastructure.repository;

import com.epam.application.repository.TrainingTypeRepository;
import com.epam.infrastructure.daos.TrainingTypeDao;
import com.epam.infrastructure.enums.TrainingTypeEnum;
import com.epam.infrastructure.mappers.TrainingTypeMapper;
import com.epam.model.TrainingType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaTrainingTypeRepository implements TrainingTypeRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private final TrainingTypeMapper trainingTypeMapper;

    @Override
    public Optional<TrainingType> findByType(TrainingTypeEnum type) {
        try {
            TrainingTypeDao result = entityManager
                    .createNamedQuery("TrainingTypeDao.findByType", TrainingTypeDao.class)
                    .setParameter("trainingType", type)
                    .getSingleResult();
            return Optional.of(trainingTypeMapper.toModel(result));
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Transactional
    @Override
    public TrainingType save(TrainingType trainingType) {
        TrainingTypeDao trainingTypeDao = trainingTypeMapper.toDao(trainingType);
        if (trainingTypeDao.getTrainingTypeId() == null) {
            entityManager.persist(trainingTypeDao);
            return trainingTypeMapper.toModel(trainingTypeDao);
        }
        return trainingTypeMapper.toModel(entityManager.merge(trainingTypeDao));
    }

    @Override
    public List<TrainingType> findAll() {
        return trainingTypeMapper.toModelList(
                entityManager.createQuery("from TrainingTypeDao", TrainingTypeDao.class).getResultList()
        );
    }
}