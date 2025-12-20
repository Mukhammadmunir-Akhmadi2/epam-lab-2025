package com.epam.infrastructure.daos;

import com.epam.infrastructure.enums.RoleEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.NamedQuery;

import java.util.UUID;

@Entity
@Table(name = "roles")
@Data
@NamedQuery(
        name = "RoleDao.findByName",
        query = "SELECT r FROM RoleDao r WHERE r.role = :role"
)
public class RoleDao {
    @Id
    @GeneratedValue
    @Column(name = "role_id", nullable = false)
    private UUID roleId;

    @Column(name = "role_name", nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private RoleEnum role;
}
