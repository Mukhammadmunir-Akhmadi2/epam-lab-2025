package com.epam.infrastructure.repository;

import com.epam.infrastructure.enums.RoleEnum;
import com.epam.model.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class JpaRoleRepositoryTest {

    @Autowired
    private PlatformTransactionManager transactionManager;

    @PersistenceContext
    EntityManager em;

    @Autowired
    private JpaRoleRepository roleRepository;

    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setRole(RoleEnum.TRAINEE);
    }

    @AfterEach
    void cleanUp() {
        TransactionTemplate tx = new TransactionTemplate(transactionManager);
        tx.execute(status -> {
            em.createQuery("DELETE FROM RoleDao").executeUpdate();
            return null;
        });
    }

    @Test
    void save_shouldPersistRole() {
        Role saved = roleRepository.save(role);

        assertNotNull(saved.getRoleId());
        assertEquals(RoleEnum.TRAINEE, saved.getRole());
    }

    @Test
    void findByName_shouldReturnRole() {
        Role saved = roleRepository.save(role);

        Optional<Role> found = roleRepository.findByName(RoleEnum.TRAINEE);

        assertTrue(found.isPresent());
        assertEquals(saved.getRoleId(), found.get().getRoleId());
        assertEquals(RoleEnum.TRAINEE, found.get().getRole());
    }

    @Test
    void save_shouldUpdateExistingRole() {
        Role saved = roleRepository.save(role);

        saved.setRole(RoleEnum.TRAINER);

        Role updated = roleRepository.save(saved);

        assertEquals(saved.getRoleId(), updated.getRoleId());
        assertEquals(RoleEnum.TRAINER, updated.getRole());
    }

    @Test
    void findAll_shouldReturnAllRoles() {
        Role trainee = new Role();
        trainee.setRole(RoleEnum.TRAINEE);

        Role trainer = new Role();
        trainer.setRole(RoleEnum.TRAINER);

        roleRepository.save(trainee);
        roleRepository.save(trainer);

        List<Role> allRoles = roleRepository.findAll();

        assertEquals(2, allRoles.size());
        assertTrue(allRoles.stream().anyMatch(r -> r.getRole() == RoleEnum.TRAINEE));
        assertTrue(allRoles.stream().anyMatch(r -> r.getRole() == RoleEnum.TRAINER));
    }
}