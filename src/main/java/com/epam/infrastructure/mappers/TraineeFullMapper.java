package com.epam.infrastructure.mappers;

import com.epam.infrastructure.daos.TraineeDao;
import com.epam.infrastructure.dtos.TraineeResponseDto;
import com.epam.model.Trainee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        uses = {CommonMapper.class, TrainerMapper.class, TraineeMapper.class, TrainingTypeMapper.class},
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TraineeFullMapper {

    @Mapping(source = "userId", target = "userId", qualifiedByName = "uuidToString")
    @Mapping(source = "trainers", target = "trainers", qualifiedByName = "mapTrainers")
    Trainee toModel(TraineeDao traineeDao);

    @Mapping(source = "userId", target = "userId", qualifiedByName = "stringToUuid")
    @Mapping(source = "trainers", target = "trainers", qualifiedByName = "mapTrainerDaos")
    @Mapping(target = "trainings", ignore = true)
    TraineeDao toDao(Trainee trainee);

    @Mapping(source = "trainers", target = "trainers", qualifiedByName = "toTrainerBriefDtoList")
    @Mapping(source = "dateOfBirth", target = "dateOfBirth", qualifiedByName = "formatLocalDate")
    TraineeResponseDto toTraineeResponseDto(Trainee trainee);

}
