package com.epam.infrastructure.mappers;

import com.epam.infrastructure.daos.TrainerDao;
import com.epam.infrastructure.dtos.TrainerResponseDto;
import com.epam.model.Trainer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        uses = {CommonMapper.class, TrainerMapper.class, TraineeMapper.class, TrainingTypeMapper.class},
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TrainerFullMapper {
    @Mapping(source = "userId", target = "userId", qualifiedByName = "uuidToString")
    @Mapping(source = "trainees", target = "trainees", qualifiedByName = "mapTrainees")
    Trainer toModel(TrainerDao trainerDao);

    @Mapping(source = "userId", target = "userId", qualifiedByName = "stringToUuid")
    @Mapping(source = "trainees", target = "trainees", qualifiedByName = "mapTraineeDaos")
    TrainerDao toDao(Trainer trainer);

    @Mapping(source = "trainees", target = "trainees", qualifiedByName = "toTraineeBriefDtoList")
    @Mapping(source = "specialization", target = "specialization", qualifiedByName = "trainingTypeToEnum")
    TrainerResponseDto toTrainerResponseDto(Trainer trainer);
}
