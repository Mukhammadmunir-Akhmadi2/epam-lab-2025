package com.epam.infrastructure.mappers;

import com.epam.infrastructure.daos.TrainingTypeDao;
import com.epam.infrastructure.dtos.TrainingTypeDto;
import com.epam.infrastructure.enums.TrainingTypeEnum;
import com.epam.model.TrainingType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(uses = CommonMapper.class, componentModel = "spring")
public interface TrainingTypeMapper {

    @Named("toModel")
    @Mapping(source = "trainingTypeId", target = "trainingTypeId", qualifiedByName = "uuidToString")
    TrainingType toModel(TrainingTypeDao trainingType);

    TrainingType toModel(TrainingTypeDto trainingTypeDto);

    @Named("toDao")
    @Mapping(source = "trainingTypeId", target = "trainingTypeId", qualifiedByName = "stringToUuid")
    TrainingTypeDao toDao(TrainingType trainingType);

    TrainingTypeDto toDto(TrainingType trainingType);

    List<TrainingTypeDto> toDtoList(List<TrainingType> trainingTypes);

    List<TrainingType> toModelList(List<TrainingTypeDao> fromTrainingTypeDao);

    //NAMED MAPPERS

    @Named("trainingTypeToEnum")
    default TrainingTypeEnum trainingTypeToEnum(TrainingType type) {
        return type == null ? null : type.getTrainingType();
    }

    @Named("enumToTrainingType")
    default TrainingType enumToTrainingType(TrainingTypeEnum typeEnum) {
        if (typeEnum == null) return null;
        TrainingType type = new TrainingType();
        type.setTrainingType(typeEnum);
        return type;
    }
}
