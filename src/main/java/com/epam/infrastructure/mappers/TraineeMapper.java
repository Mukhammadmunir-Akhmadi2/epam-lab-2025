package com.epam.infrastructure.mappers;

import com.epam.infrastructure.daos.TraineeDao;
import com.epam.model.Trainee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = CommonMapper.class)
public interface TraineeMapper {
    TraineeMapper INSTANCE = Mappers.getMapper(TraineeMapper.class);


    @Mapping(source = "userId", target = "userId", qualifiedByName = "uuidToString")
    @Mapping(source = "dateOfBirth", target = "dateOfBirth", qualifiedByName = "stringToLocalDate")
    Trainee toModel(TraineeDao traineeDao);

    @Mapping(source = "userId", target = "userId", qualifiedByName = "stringToUuid")
    @Mapping(source = "dateOfBirth", target = "dateOfBirth", qualifiedByName = "localDateToString")
    TraineeDao toDao(Trainee trainee);

    List<Trainee> toModelList(List<TraineeDao> trainees);

}
