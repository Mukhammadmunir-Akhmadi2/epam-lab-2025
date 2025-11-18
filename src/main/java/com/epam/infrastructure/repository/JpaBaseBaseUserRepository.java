package com.epam.infrastructure.repository;

import com.epam.application.repository.BaseUserRepository;
import com.epam.infrastructure.daos.UserDao;
import com.epam.infrastructure.mappers.UserMapper;
import com.epam.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaBaseBaseUserRepository implements BaseUserRepository {

    private final UserMapper userMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<User> findByUserName(String username) {
        try {
            UserDao userDao = entityManager.createNamedQuery("UserDao.findByUsername", UserDao.class)
                    .setParameter("username", username)
                    .getSingleResult();

            return Optional.of(userMapper.toModel(userDao));
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public User save(User user) {
        UserDao userDao = userMapper.toDao(user);

        if (userDao.getUserId() == null) {
            throw new IllegalArgumentException("Cannot update user without ID");
        }
        return userMapper.toModel(entityManager.merge(userDao));
    }
}
