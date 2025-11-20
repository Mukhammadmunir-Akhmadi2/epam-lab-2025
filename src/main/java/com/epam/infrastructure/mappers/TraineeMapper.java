package com.epam.infrastructure.mappers;

import com.epam.infrastructure.daos.TraineeDao;
import com.epam.model.Trainee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(uses = {CommonMapper.class},
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TraineeMapper {

    @Mapping(source = "userId", target = "userId", qualifiedByName = "uuidToString")
    @Mapping(target = "trainers", ignore = true)
    Trainee toModel(TraineeDao traineeDao);

    @Mapping(source = "userId", target = "userId", qualifiedByName = "stringToUuid")
    TraineeDao toDao(Trainee trainee);

    List<Trainee> toModelList(List<TraineeDao> trainees);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    void updateFields(TraineeDao model, @MappingTarget TraineeDao dao);
}

