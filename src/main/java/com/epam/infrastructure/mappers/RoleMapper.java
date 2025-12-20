package com.epam.infrastructure.mappers;

import com.epam.infrastructure.daos.RoleDao;
import com.epam.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        uses = {CommonMapper.class},
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface RoleMapper {

    @Mapping(source = "roleId", target = "roleId", qualifiedByName = "uuidToString")
    Role toModel(RoleDao roleDao);

    @Mapping(source = "roleId", target = "roleId", qualifiedByName = "stringToUuid")
    RoleDao toDao(Role role);

    List<Role> toModelList(List<RoleDao> roleDaos);
    List<RoleDao> toDaoList(List<Role> roles);
}
