package com.epam.infrastructure.mappers;

import com.epam.infrastructure.daos.TrainerDao;
import com.epam.model.Trainer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = CommonMapper.class)
public interface TrainerMapper {
    TrainerMapper INSTANCE = Mappers.getMapper(TrainerMapper.class);

    @Mapping(source = "userId", target = "userId", qualifiedByName = "stringToUuid")
    Trainer toModel(TrainerDao trainerDao);

    @Mapping(source = "userId", target = "userId", qualifiedByName = "uuidToString")
    TrainerDao toDao(Trainer trainer);

    List<Trainer> toModelList(List<TrainerDao> trainers);
}
