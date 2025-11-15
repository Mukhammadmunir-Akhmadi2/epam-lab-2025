package com.epam.infrastructure.daos;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class UserDao {
    @Id
    @GeneratedValue
    @Column(name = "user_id")
    protected UUID userId;

    @Column(name = "first_name", nullable = false, length = 50)
    protected String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    protected String lastName;

    @Column(name = "username", nullable = false, unique = true, length = 100)
    protected String userName;

    @Column(name = "password", nullable = false, length = 18)
    protected String password;

    @Column(name = "is_active", nullable = false)
    protected boolean isActive;
}
