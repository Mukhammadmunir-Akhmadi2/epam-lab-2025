package com.epam.infrastructure.repository;

import com.epam.application.repository.RoleRepository;
import com.epam.infrastructure.annotations.MapStorage;
import com.epam.infrastructure.daos.RoleDao;
import com.epam.infrastructure.enums.RoleEnum;
import com.epam.infrastructure.mappers.RoleMapper;
import com.epam.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@MapStorage(file = "role.json")
@RequiredArgsConstructor
@Profile("dev")
public class InMemoryRoleRepository implements RoleRepository {

    private final Map<String, RoleDao> storage = new HashMap<>();
    private final RoleMapper mapper;

    @Override
    public Optional<Role> findByName(RoleEnum role) {
        return storage.values().stream()
                .filter(r -> r.getRole().equals(role))
                .findFirst()
                .map(mapper::toModel);
    }

    @Override
    public Role save(Role role) {
        if (role.getRoleId() == null) {
            role.setRoleId(UUID.randomUUID().toString());
        }
        storage.put(role.getRoleId(), mapper.toDao(role));
        return mapper.toModel(storage.get(role.getRoleId()));
    }

    @Override
    public List<Role> findAll() {
        return mapper.toModelList(new ArrayList<>(storage.values()));
    }
}