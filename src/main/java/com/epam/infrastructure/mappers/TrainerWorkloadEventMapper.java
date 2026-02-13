package com.epam.infrastructure.mappers;

import com.epam.infrastructure.dtos.TrainerWorkloadRequestDto;
import com.epam.infrastructure.enums.ActionType;
import com.epam.model.Trainer;
import com.epam.model.Training;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TrainerWorkloadEventMapper {
    default TrainerWorkloadRequestDto toDto(Training training, ActionType actionType) {
        Trainer trainer = training.getTrainer();
        TrainerWorkloadRequestDto req = new TrainerWorkloadRequestDto();
        req.setTrainerUsername(trainer.getUsername());
        req.setTrainerFirstName(trainer.getFirstName());
        req.setTrainerLastName(trainer.getLastName());
        req.setIsActive(trainer.getIsActive());
        req.setTrainingDate(training.getDate().toLocalDate());
        req.setTrainingDuration(training.getDuration());
        req.setActionType(actionType);

        return req;
    }
}
