package com.epam.infrastructure.repository;

import com.epam.application.repository.RoleRepository;
import com.epam.infrastructure.daos.RoleDao;
import com.epam.infrastructure.enums.RoleEnum;
import com.epam.infrastructure.mappers.RoleMapper;
import com.epam.model.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Profile({"local", "stg", "test", "prod"})
public class JpaRoleRepository implements RoleRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    private final RoleMapper mapper;

    @Override
    public Optional<Role> findByName(RoleEnum role) {
        try {
            RoleDao dao = entityManager
                    .createNamedQuery("RoleDao.findByName", RoleDao.class)
                    .setParameter("role", role)
                    .getSingleResult();

            return Optional.of(mapper.toModel(dao));
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @Transactional
    @Override
    public Role save(Role role) {
        RoleDao dao = mapper.toDao(role);

        if (dao.getRoleId() == null) {
            entityManager.persist(dao);
            return mapper.toModel(dao);
        }

        return mapper.toModel(entityManager.merge(dao));
    }

    @Override
    public List<Role> findAll() {
        return mapper.toModelList(entityManager
                .createQuery("from RoleDao", RoleDao.class)
                .getResultList());
    }
}