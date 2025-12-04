package com.epam.infrastructure.mappers;

import com.epam.infrastructure.daos.UserDao;
import com.epam.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("dev")
class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);
    }

    @Test
    void testToModel() {
        UUID userId = UUID.randomUUID();
        UserDao userDao = new UserDao(
                userId,
                "John",
                "Doe",
                "john_doe",
                "password123",
                true
        );

        User user = userMapper.toModel(userDao);

        assertNotNull(user);
        assertEquals(userId.toString(), user.getUserId());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("john_doe", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertTrue(user.isActive());
    }

    @Test
    void testToDao() {
        String userIdStr = UUID.randomUUID().toString();
        User user = new User(
                userIdStr,
                "Jane",
                "Doe",
                "jane_doe",
                "securePass",
                false
        );

        UserDao userDao = userMapper.toDao(user);

        assertNotNull(userDao);
        assertEquals(UUID.fromString(userIdStr), userDao.getUserId());
        assertEquals("Jane", userDao.getFirstName());
        assertEquals("Doe", userDao.getLastName());
        assertEquals("jane_doe", userDao.getUsername());
        assertEquals("securePass", userDao.getPassword());
        assertFalse(userDao.isActive());
    }

    @Test
    void testUpdateFields() {
        UserDao originalDao = new UserDao(
                UUID.randomUUID(),
                "John",
                "Doe",
                "john_doe",
                "password123",
                true
        );

        UserDao updateDao = new UserDao(
                UUID.randomUUID(),
                "UpdatedFirstName",
                "UpdatedLastName",
                "updated_username",
                "newPassword",
                false
        );

        userMapper.updateFields(updateDao, originalDao);

        assertNotEquals(updateDao.getUserId(), originalDao.getUserId());
        assertNotEquals(updateDao.getUsername(), originalDao.getUsername());

        assertEquals("UpdatedFirstName", originalDao.getFirstName());
        assertEquals("UpdatedLastName", originalDao.getLastName());
        assertEquals("newPassword", originalDao.getPassword());
        assertFalse(originalDao.isActive());
    }
}
