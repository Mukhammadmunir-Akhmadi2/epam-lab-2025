package com.epam.infrastructure.mappers;

import com.epam.infrastructure.daos.TrainerDao;
import com.epam.infrastructure.dtos.AuthDto;
import com.epam.infrastructure.dtos.TrainerBriefDto;
import com.epam.infrastructure.dtos.TrainerDto;
import com.epam.infrastructure.dtos.TrainerRegistrationRequest;
import com.epam.model.Trainer;
import com.epam.model.TrainingType;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(
        uses = {CommonMapper.class, TrainingTypeMapper.class},
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TrainerMapper {

    //DAO → MODEL
    @Named("toBriefModel")
    @Mapping(target = "trainees", ignore = true)
    Trainer toBriefModel(TrainerDao trainerDao);

    default Trainer toModel(TrainerDto trainerDto, TrainingType trainingType) {
        if (trainerDto == null) return null;
        Trainer trainer = new Trainer();
        trainer.setUsername(trainerDto.getUsername());
        trainer.setFirstName(trainerDto.getFirstName());
        trainer.setLastName(trainerDto.getLastName());
        trainer.setActive(trainerDto.isActive());
        trainer.setSpecialization(trainingType);
        return trainer;
    };

    //DTOs

    @Mapping(source = "specialization", target = "specialization", qualifiedByName = "trainingTypeToEnum")
    TrainerBriefDto toBriefDto(Trainer trainer);

    AuthDto toAuthDto(Trainer trainee);

    //COLLECTION

    @IterableMapping(qualifiedByName = "toBriefModel")
    List<Trainer> toModelList(List<TrainerDao> trainers);

    //UPDATE PATCH

    @Mapping(target = "trainees", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "username", ignore = true)
    void updateFields(TrainerDao model, @MappingTarget TrainerDao dao);


    //CUSTOM CONSTRUCTOR

    default Trainer toModel(TrainerRegistrationRequest request, TrainingType trainingType) {
        Trainer trainer = new Trainer();
        trainer.setFirstName(request.getFirstName());
        trainer.setLastName(request.getLastName());
        trainer.setSpecialization(trainingType);
        return trainer;
    }

    //NAMED MAPPERS

    @Named("toTrainerDao")
    @Mapping(source = "userId", target = "userId", qualifiedByName = "stringToUuid")
    @Mapping(source = "specialization", target = "specialization", qualifiedByName = "toDao")
    @Mapping(target = "trainees", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "active", ignore = true)
    TrainerDao toTrainerDao(Trainer trainer);

    @Named("toTrainer")
    @Mapping(source = "userId", target = "userId", qualifiedByName = "uuidToString")
    @Mapping(source = "specialization", target = "specialization", qualifiedByName = "toModel")
    @Mapping(target = "trainees", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "active", ignore = true)
    Trainer toTrainer(TrainerDao dao);

    @Named("mapTrainers")
    @IterableMapping(qualifiedByName = "toTrainer")
    Set<Trainer> mapTrainers(Set<TrainerDao> trainerDaos);

    @Named("mapTrainerDaos")
    @IterableMapping(qualifiedByName = "toTrainerDao")
    Set<TrainerDao> mapTrainerDaos(Set<Trainer> trainers);

    @Named("toTrainerBriefDtoList")
    List<TrainerBriefDto> toTrainerBriefDtoList(Set<Trainer> trainers);

    @Named("toTrainerBriefDtoListFromList")
    List<TrainerBriefDto> toTrainerBriefDtoList(List<Trainer> trainers);
}
