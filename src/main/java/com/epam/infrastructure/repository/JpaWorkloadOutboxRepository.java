package com.epam.infrastructure.repository;

import com.epam.infrastructure.outbox.WorkloadOutboxRepository;
import com.epam.infrastructure.daos.WorkloadOutboxEventDao;
import com.epam.infrastructure.mappers.WorkloadOutboxMapper;
import com.epam.infrastructure.outbox.entity.WorkloadOutboxEvent;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaWorkloadOutboxRepository implements WorkloadOutboxRepository {

    private final WorkloadOutboxMapper mapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public WorkloadOutboxEvent save(WorkloadOutboxEvent event) {
        WorkloadOutboxEventDao dao = mapper.toDao(event);

        if (dao.getWoeId() == null) {
            entityManager.persist(dao);
            return mapper.toModel(dao);
        }

        WorkloadOutboxEventDao existing =
                entityManager.find(WorkloadOutboxEventDao.class, dao.getWoeId());

        if (existing == null) {
            throw new EntityNotFoundException("Outbox event not found");
        }

        existing.setStatus(dao.getStatus());
        existing.setAttempts(dao.getAttempts());
        existing.setNextAttemptAt(dao.getNextAttemptAt());
        existing.setLastError(dao.getLastError());

        return mapper.toModel(entityManager.merge(existing));
    }

    @Override
    public Optional<WorkloadOutboxEvent> findById(String id) {
        WorkloadOutboxEventDao dao =
                entityManager.find(WorkloadOutboxEventDao.class, UUID.fromString(id));
        return Optional.ofNullable(dao).map(mapper::toModel);
    }

    @Override
    public List<WorkloadOutboxEvent> findBatchForRetry(LocalDateTime now) {
        List<WorkloadOutboxEventDao> result =
                entityManager
                        .createNamedQuery(
                                "WorkloadOutboxEventDao.findBatchForRetry",
                                WorkloadOutboxEventDao.class
                        )
                        .setParameter("now", now)
                        .getResultList();

        return mapper.toModelList(result);
    }

    @Transactional
    @Override
    public void delete(String id) {
        WorkloadOutboxEventDao dao =
                entityManager.find(WorkloadOutboxEventDao.class, UUID.fromString(id));
        if (dao != null) {
            entityManager.remove(dao);
        }
    }
}
