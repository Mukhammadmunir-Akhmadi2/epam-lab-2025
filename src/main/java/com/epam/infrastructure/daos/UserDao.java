package com.epam.infrastructure.daos;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
@NamedQuery(name = "UserDao.findByUsername",
        query = "SELECT u FROM UserDao u LEFT JOIN FETCH u.roles WHERE u.username = :username"
)
public class UserDao {
    @Id
    @GeneratedValue
    @Column(name = "user_id")
    protected UUID userId;

    @Column(name = "first_name", nullable = false, length = 50)
    protected String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    protected String lastName;

    @Column(name = "username", nullable = false, unique = true, length = 100)
    protected String username;

    @Column(name = "password", nullable = false, length = 60)
    protected String password;

    @Column(name = "is_active", nullable = false)
    protected Boolean isActive;

    @ManyToMany
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "role_id", nullable = false)
    )
    protected Set<RoleDao> roles = new HashSet<>();
}
