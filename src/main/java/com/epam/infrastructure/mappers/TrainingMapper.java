package com.epam.infrastructure.mappers;

import com.epam.infrastructure.daos.TrainingDao;
import com.epam.model.Training;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = CommonMapper.class)
public interface TrainingMapper {
    TrainingMapper INSTANCE = Mappers.getMapper(TrainingMapper.class);

    @Mapping(source = "trainingId", target = "trainingId", qualifiedByName = "uuidToString")
    @Mapping(source = "date", target = "date", qualifiedByName = "stringToLocalDateTime")
    Training toModel(TrainingDao trainingDao);

    @Mapping(source = "trainingId", target = "trainingId", qualifiedByName = "stringToUuid")
    @Mapping(source = "date", target = "date", qualifiedByName = "localDateTimeToString")
    TrainingDao toDao(Training training);

    List<Training> toModelList(List<TrainingDao> trainingDaoList);
}
