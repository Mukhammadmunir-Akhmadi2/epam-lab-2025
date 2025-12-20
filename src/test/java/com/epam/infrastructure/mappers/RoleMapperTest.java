package com.epam.infrastructure.mappers;
import com.epam.infrastructure.daos.RoleDao;
import com.epam.infrastructure.enums.RoleEnum;
import com.epam.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
class RoleMapperTest {

    private RoleMapper roleMapper;

    @BeforeEach
    void setUp() {
        roleMapper = Mappers.getMapper(RoleMapper.class);
    }

    @Test
    void testToModel() {
        UUID id = UUID.randomUUID();

        RoleDao dao = new RoleDao();
        dao.setRoleId(id);
        dao.setRole(RoleEnum.TRAINEE);

        Role model = roleMapper.toModel(dao);

        assertNotNull(model);
        assertEquals(id.toString(), model.getRoleId());
        assertEquals(RoleEnum.TRAINEE, model.getRole());
    }

    @Test
    void testToDao() {
        String id = UUID.randomUUID().toString();

        Role model = new Role();
        model.setRoleId(id);
        model.setRole(RoleEnum.TRAINER);

        RoleDao dao = roleMapper.toDao(model);

        assertNotNull(dao);
        assertEquals(UUID.fromString(id), dao.getRoleId());
        assertEquals(RoleEnum.TRAINER, dao.getRole());
    }

    @Test
    void testListToModelList() {
        RoleDao dao1 = new RoleDao();
        dao1.setRoleId(UUID.randomUUID());
        dao1.setRole(RoleEnum.TRAINEE);

        RoleDao dao2 = new RoleDao();
        dao2.setRoleId(UUID.randomUUID());
        dao2.setRole(RoleEnum.TRAINER);

        List<Role> models = roleMapper.toModelList(List.of(dao1, dao2));

        assertEquals(2, models.size());
        assertTrue(models.stream().anyMatch(r -> r.getRole() == RoleEnum.TRAINEE));
        assertTrue(models.stream().anyMatch(r -> r.getRole() == RoleEnum.TRAINER));
    }

    @Test
    void testListToDaoList() {
        Role role1 = new Role(UUID.randomUUID().toString(), RoleEnum.TRAINEE);
        Role role2 = new Role(UUID.randomUUID().toString(), RoleEnum.TRAINER);

        List<RoleDao> daos = roleMapper.toDaoList(List.of(role1, role2));

        assertEquals(2, daos.size());
        assertTrue(daos.stream().anyMatch(r -> r.getRole() == RoleEnum.TRAINEE));
        assertTrue(daos.stream().anyMatch(r -> r.getRole() == RoleEnum.TRAINER));
    }

    @Test
    void testNullSafe() {
        assertNull(roleMapper.toModel(null));
        assertNull(roleMapper.toDao(null));
        assertNotNull(roleMapper.toModelList(List.of()));
        assertNotNull(roleMapper.toDaoList(List.of()));
    }
}