package com.epam.infrastructure.mappers;

import com.epam.infrastructure.daos.TraineeDao;
import com.epam.infrastructure.daos.TrainerDao;
import com.epam.infrastructure.daos.TrainingDao;
import com.epam.model.Trainee;
import com.epam.model.Trainer;
import com.epam.model.Training;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@Mapper(uses = {CommonMapper.class}, componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class TrainingMapper {

    @Autowired
    protected TrainingTypeMapper trainingTypeMapper;

    @Mapping(source = "trainingId", target = "trainingId", qualifiedByName = "uuidToString")
    @Mapping(source = "trainer", target = "trainer", qualifiedByName = "toTrainer")
    @Mapping(source = "trainee", target = "trainee", qualifiedByName = "toTrainee")
    public abstract Training toFullModel(TrainingDao trainingDao);

    @Mapping(source = "trainingId", target = "trainingId", qualifiedByName = "uuidToString")
    @Mapping(source = "trainer", target = "trainer", ignore = true)
    @Mapping(source = "trainee", target = "trainee", ignore = true)
    @Named("toModel")
    public abstract Training toModel(TrainingDao trainingDao);

    @Mapping(source = "trainingId", target = "trainingId", qualifiedByName = "stringToUuid")
    @Mapping(source = "trainer", target = "trainer", qualifiedByName = "toTrainerDao")
    @Mapping(source = "trainee", target = "trainee", qualifiedByName = "toTraineeDao")
    public abstract TrainingDao toFullDao(Training training);

    @Mapping(source = "trainingId", target = "trainingId", qualifiedByName = "stringToUuid")
    @Mapping(source = "trainer", target = "trainer", qualifiedByName = "toTrainerDao")
    @Mapping(source = "trainee", target = "trainee", ignore = true)
    public abstract TrainingDao toDao(Training training);

    @IterableMapping(qualifiedByName = "toModel")
    public abstract List<Training> toModelList(List<TrainingDao> trainingDaoList);

    @Mapping(target = "trainee", ignore = true)
    @Mapping(target = "trainer", ignore = true)
    @Mapping(target = "trainingId", ignore = true)
    public abstract void updateFields(TrainingDao model, @MappingTarget TrainingDao dao);

    @Named("toTrainer")
    public Trainer toTrainer(TrainerDao trainerDao) {
        if (trainerDao == null) {
            return null;
        }
        Trainer trainer = new Trainer();
        trainer.setUserId(trainerDao.getUserId().toString());
        trainer.setUserName(trainerDao.getUserName());
        trainer.setFirstName(trainerDao.getFirstName());
        trainer.setLastName(trainerDao.getLastName());
        trainer.setSpecialization(
                trainingTypeMapper.toModel(trainerDao.getSpecialization())
        );
        return trainer;
    }

    @Named("toTrainerDao")
    public TrainerDao toTrainerDao(Trainer trainer) {
        if (trainer == null) {
            return null;
        }
        TrainerDao trainerDao = new TrainerDao();
        trainerDao.setUserId(UUID.fromString(trainer.getUserId()));
        trainerDao.setUserName(trainer.getUserName());
        trainerDao.setFirstName(trainer.getFirstName());
        trainerDao.setLastName(trainer.getLastName());
        trainerDao.setSpecialization(
                trainingTypeMapper.toDao(trainer.getSpecialization())
        );
        return trainerDao;
    }

    @Named("toTrainee")
    public Trainee toTrainee(TraineeDao traineeDao) {
        if (traineeDao == null) {
            return null;
        }
        Trainee trainee = new Trainee();
        trainee.setUserId(traineeDao.getUserId().toString());
        trainee.setUserName(traineeDao.getUserName());
        trainee.setFirstName(traineeDao.getFirstName());
        trainee.setLastName(traineeDao.getLastName());
        return trainee;
    }

    @Named("toTraineeDao")
    public TraineeDao toTraineeDao(Trainee trainee) {
        if (trainee == null) {
            return null;
        }
        TraineeDao traineeDao = new TraineeDao();
        traineeDao.setUserId(UUID.fromString(trainee.getUserId()));
        traineeDao.setUserName(trainee.getUserName());
        traineeDao.setFirstName(trainee.getFirstName());
        traineeDao.setLastName(trainee.getLastName());
        return traineeDao;
    }
}