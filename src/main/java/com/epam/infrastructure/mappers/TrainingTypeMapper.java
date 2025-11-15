package com.epam.infrastructure.mappers;

import com.epam.infrastructure.daos.TrainingTypeDao;
import com.epam.model.TrainingType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = CommonMapper.class, componentModel = "spring")
public interface TrainingTypeMapper {

    @Mapping(source = "trainingTypeId", target = "trainingTypeId", qualifiedByName = "uuidToString")
    TrainingType toModel(TrainingTypeDao trainingType);

    @Mapping(source = "trainingTypeId", target = "trainingTypeId", qualifiedByName = "stringToUuid")
    TrainingTypeDao toDao(TrainingType trainingType);
}
