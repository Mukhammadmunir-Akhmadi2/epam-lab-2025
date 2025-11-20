package com.epam.infrastructure.mappers;

import com.epam.infrastructure.daos.TrainerDao;
import com.epam.model.Trainer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        uses = {CommonMapper.class, TrainingTypeMapper.class},
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TrainerMapper {

    @Mapping(source = "userId", target = "userId", qualifiedByName = "uuidToString")
    @Mapping(target = "trainees", ignore = true)
    Trainer toModel(TrainerDao trainerDao);

    @Mapping(source = "userId", target = "userId", qualifiedByName = "stringToUuid")
    @Mapping(target = "trainees", ignore = true)
    TrainerDao toDao(Trainer trainer);

    @Mapping(target = "trainees", ignore = true)
    @Mapping(target = "userId", ignore = true)
    void updateFields(TrainerDao model, @MappingTarget TrainerDao dao);

    List<Trainer> toModelList(List<TrainerDao> trainers);
}
