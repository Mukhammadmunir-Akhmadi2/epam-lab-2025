package com.epam.infrastructure.mappers;

import com.epam.infrastructure.daos.TrainingDao;
import com.epam.infrastructure.dtos.TraineeTrainingDto;
import com.epam.infrastructure.dtos.TrainerTrainingDto;
import com.epam.infrastructure.dtos.TrainingDto;
import com.epam.model.Trainee;
import com.epam.model.Trainer;
import com.epam.model.Training;
import com.epam.model.TrainingType;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        uses = {CommonMapper.class, TraineeMapper.class, TrainerMapper.class},
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TrainingMapper {

    @Mapping(source = "trainingId", target = "trainingId", qualifiedByName = "uuidToString")
    @Mapping(source = "trainer", target = "trainer", qualifiedByName = "toTrainer")
    @Mapping(source = "trainee", target = "trainee", qualifiedByName = "toTrainee")
    @Named("toFullModel")
    Training toFullModel(TrainingDao trainingDao);

    @Mapping(source = "trainingId", target = "trainingId", qualifiedByName = "uuidToString")
    @Mapping(source = "trainer", target = "trainer", ignore = true)
    @Mapping(source = "trainee", target = "trainee", ignore = true)
    @Named("toModel")
    Training toModel(TrainingDao trainingDao);

    @Mapping(source = "trainingId", target = "trainingId", qualifiedByName = "stringToUuid")
    @Mapping(source = "trainer", target = "trainer", qualifiedByName = "toTrainerDao")
    @Mapping(source = "trainee", target = "trainee", qualifiedByName = "toTraineeDao")
    TrainingDao toFullDao(Training training);

    @IterableMapping(qualifiedByName = "toModel")
    List<Training> toModelList(List<TrainingDao> trainingDaoList);

    @IterableMapping(qualifiedByName = "toFullModel")
    List<Training> toFullModelList(List<TrainingDao> trainingDaoList);

    @Mapping(target = "trainee", ignore = true)
    @Mapping(target = "trainer", ignore = true)
    @Mapping(target = "trainingId", ignore = true)
    void updateFields(TrainingDao model, @MappingTarget TrainingDao dao);

    default Training toModel(Trainee trainee, Trainer trainer, TrainingType trainingType, TrainingDto request) {
        Training training = toModel(new TrainingDao());
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);

        training.setTrainingName(request.getName());
        training.setDate(CommonMapper.parseLocalDateTime(request.getDate()));
        training.setDuration(request.getDuration());
        return training;
    }

    default List<TrainerTrainingDto> toTrainerTrainingDtoList(List<Training> traineeTrainings) {
        return traineeTrainings.stream().map(training -> {
            TrainerTrainingDto dto = new TrainerTrainingDto();
            dto.setName(training.getTrainingName());
            dto.setDate(CommonMapper.formatLocalDateTime(training.getDate()));
            dto.setDuration(training.getDuration());
            dto.setType(training.getTrainingType().getTrainingType());
            dto.setTrainerUsername(training.getTrainee().getUsername());
            return dto;
        }).toList();
    }

    default List<TraineeTrainingDto> toTraineeTrainingDtoList(List<Training> trainerTrainings) {
        return trainerTrainings.stream().map(training -> {
            TraineeTrainingDto dto = new TraineeTrainingDto();
            dto.setName(training.getTrainingName());
            dto.setDate(CommonMapper.formatLocalDateTime(training.getDate()));
            dto.setDuration(training.getDuration());
            dto.setType(training.getTrainingType().getTrainingType());
            dto.setTraineeUsername(training.getTrainee().getUsername());
            return dto;
        }).toList();
    }
}