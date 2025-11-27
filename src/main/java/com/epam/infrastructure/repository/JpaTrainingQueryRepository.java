package com.epam.infrastructure.repository;

import com.epam.application.repository.TrainingQueryRepository;
import com.epam.infrastructure.daos.TrainingDao;
import com.epam.infrastructure.enums.TrainingTypeEnum;
import com.epam.infrastructure.mappers.TrainingMapper;
import com.epam.model.Training;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaTrainingQueryRepository implements TrainingQueryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private final TrainingMapper trainingMapper;

    @Override
    public List<Training> findTrainingsByTraineeUsernameWithFilters(String traineeUsername, LocalDate from, LocalDate to, String trainerName, TrainingTypeEnum trainingType) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TrainingDao> cq = cb.createQuery(TrainingDao.class);
        Root<TrainingDao> trainingRoot = cq.from(TrainingDao.class);

        trainingRoot.fetch("trainee");
        trainingRoot.fetch("trainer");
        trainingRoot.fetch("trainingType");

        Predicate predicate = cb.equal(trainingRoot.get("trainee").get("username"), traineeUsername);

        predicate = getPredicate(from, to, cb, trainingRoot, predicate);

        if (trainerName != null && !trainerName.isBlank()) {
            predicate = cb.and(predicate, cb.like(trainingRoot.get("trainer").get("username"), "%" + trainerName + "%"));
        }

        if (trainingType != null) {
            predicate = cb.and(predicate, cb.equal(trainingRoot.get("trainingType").get("trainingType"), trainingType));
        }


        cq.select(trainingRoot).where(predicate).distinct(true);

        return trainingMapper.toFullModelList(entityManager.createQuery(cq).getResultList());
    }

    @Override
    public List<Training> findTrainingsByTrainerUsernameWithFilters(String trainerUsername, LocalDate from, LocalDate to, String traineeName) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TrainingDao> cq = cb.createQuery(TrainingDao.class);
        Root<TrainingDao> trainingRoot = cq.from(TrainingDao.class);

        trainingRoot.fetch("trainee");
        trainingRoot.fetch("trainer");
        trainingRoot.fetch("trainingType");

        Predicate predicate = cb.equal(trainingRoot.get("trainer").get("username"), trainerUsername);

        predicate = getPredicate(from, to, cb, trainingRoot, predicate);

        if (traineeName != null && !traineeName.isBlank()) {
            predicate = cb.and(predicate, cb.like(trainingRoot.get("trainee").get("username"), "%" + traineeName + "%"));
        }

        cq.select(trainingRoot).where(predicate).distinct(true);


        return trainingMapper.toFullModelList(entityManager.createQuery(cq).getResultList());
    }

    private Predicate getPredicate(LocalDate from, LocalDate to, CriteriaBuilder cb, Root<TrainingDao> trainingRoot, Predicate predicate) {
        if (from != null) {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(trainingRoot.get("date"), from.atStartOfDay()));
        }

        if (to != null) {
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(trainingRoot.get("date"), to.atTime(23, 59, 59)));
        }
        return predicate;
    }
}

