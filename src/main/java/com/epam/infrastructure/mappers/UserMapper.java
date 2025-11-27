package com.epam.infrastructure.mappers;

import com.epam.infrastructure.daos.UserDao;
import com.epam.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {CommonMapper.class})
public interface UserMapper {

    @Mapping(source = "userId", target = "userId", qualifiedByName = "uuidToString")
    User toModel(UserDao userDao);

    @Mapping(source = "userId", target = "userId", qualifiedByName = "stringToUuid")
    UserDao toDao(User user);


    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "username", ignore = true)
    void updateFields(UserDao model, @MappingTarget UserDao dao);
}
