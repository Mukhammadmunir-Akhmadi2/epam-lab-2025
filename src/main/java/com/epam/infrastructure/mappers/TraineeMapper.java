package com.epam.infrastructure.mappers;

import com.epam.infrastructure.daos.TraineeDao;
import com.epam.infrastructure.dtos.AuthDto;
import com.epam.infrastructure.dtos.TraineeBriefDto;
import com.epam.infrastructure.dtos.TraineeDto;
import com.epam.infrastructure.dtos.TraineeRegistrationRequest;
import com.epam.model.Trainee;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(
        uses = {CommonMapper.class, TrainingTypeMapper.class},
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TraineeMapper {

    //DAO → MODEL
    @Named("toBriefModel")
    @Mapping(target = "trainers", ignore = true)
    Trainee toBriefModel(TraineeDao traineeDao);

    //REQUEST / DTO → MODEL

    @Mapping(target = "userId", ignore = true)
    @Mapping(source = "dateOfBirth", target = "dateOfBirth", qualifiedByName = "parseLocalDate")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "trainers", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "active", ignore = true)
    Trainee toModel(TraineeRegistrationRequest request);

    @Mapping(target = "userId", ignore = true)
    @Mapping(source = "dateOfBirth", target = "dateOfBirth", qualifiedByName = "parseLocalDate")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "trainers", ignore = true)
    Trainee toModel(TraineeDto traineeDto);

    //MODEL → DTOs

    TraineeBriefDto toBriefDto(Trainee trainee);

    AuthDto toAuthDto(Trainee trainee);

    //COLLECTION

    @IterableMapping(qualifiedByName = "toBriefModel")
    List<Trainee> toModelList(List<TraineeDao> trainees);


    //UPDATE PATCH

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "username", ignore = true)
    void updateFields(TraineeDao model, @MappingTarget TraineeDao dao);

    //NAMED MAPPERS

    @Named("toTraineeDao")
    @Mapping(source = "userId", target = "userId", qualifiedByName = "stringToUuid")
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "dateOfBirth", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "trainers", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "active", ignore = true)
    TraineeDao toTraineeDao(Trainee trainee);

    @Named("toTrainee")
    @Mapping(source = "userId", target = "userId", qualifiedByName = "uuidToString")
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "dateOfBirth", ignore = true)
    @Mapping(target = "trainers", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "active", ignore = true)
    Trainee toTrainee(TraineeDao dao);

    @Named("mapTrainees")
    @IterableMapping(qualifiedByName = "toTrainee")
    Set<Trainee> mapTrainees(Set<TraineeDao> traineeDaos);

    @Named("mapTraineeDaos")
    @IterableMapping(qualifiedByName = "toTraineeDao")
    Set<TraineeDao> mapTraineeDaos(Set<Trainee> trainees);

    @Named("toTraineeBriefDtoList")
    List<TraineeBriefDto> toTraineeBriefDtoList(Set<Trainee> trainees);

    @Named("toTraineeBriefDtoListFromList")
    List<TraineeBriefDto> toTraineeBriefDtoList(List<Trainee> trainees);
}
