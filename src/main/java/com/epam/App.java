package com.epam;

import com.epam.application.facade.TraineeProfileFacade;
import com.epam.application.facade.TrainerProfileFacade;
import com.epam.infrastructure.config.AppConfig;
import com.epam.model.Trainee;
import com.epam.model.Trainer;
import com.epam.model.Training;
import com.epam.model.TrainingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main( String[] args ) {
//        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
//
//        try {
//            var traineeProfile = context.getBean(TraineeProfileFacade.class);
//            var trainerProfile = context.getBean(TrainerProfileFacade.class);
//
//            logger.info("-------------------------------------------------");
//            logger.info("CREATING TRAINEE #1");
//            logger.info("-------------------------------------------------");
//
//            Trainee trainee = new Trainee();
//            trainee.setFirstName("John");
//            trainee.setLastName("Doe");
//            trainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
//
//            Trainee newTrainee = traineeProfile.traineeRegistration(trainee);
//            traineeProfile.login(newTrainee.getUserName(), newTrainee.getPassword());
//
//            logger.info("Trainee registered and logged in: {} {}", newTrainee.getFirstName(), newTrainee.getUserName());
//
//            logger.info("-------------------------------------------------");
//            logger.info("CREATING TRAINER #1");
//            logger.info("-------------------------------------------------");
//
//            TrainingType spec = new TrainingType();
//            spec.setTrainingType("Java Programming");
//
//            Trainer trainer = new Trainer();
//            trainer.setFirstName("Alice");
//            trainer.setLastName("Smith");
//            trainer.setSpecialization(spec);
//
//            Trainer newTrainer = trainerProfile.trainerRegistration(trainer);
//            logger.info("Trainer registered: {} {}", newTrainer.getFirstName(), newTrainer.getLastName());
//
//            trainerProfile.login(newTrainer.getUserName(), newTrainer.getPassword());
//
//            logger.info("-------------------------------------------------");
//            logger.info("ADDING TRAINING FOR TRAINER #1");
//            logger.info("-------------------------------------------------");
//
//            Training training1 = new Training();
//            training1.setTrainingName("Java Basics");
//            training1.setTrainingType(spec);
//            training1.setDate(LocalDateTime.of(2024, 7, 1, 10, 0));
//            training1.setDuration(60);
//            training1.setTrainer(newTrainer);
//
//            trainerProfile.addTraining(newTrainer.getUserName(), newTrainee.getUserName(), training1);
//
//            trainerProfile.getTrainerTrainings(newTrainer.getUserName(), null, null, null)
//                    .forEach(tr -> logger.info("Trainer's Training: {} | Type: {} | Date: {}",
//                            tr.getTrainingName(), tr.getTrainingType().getTrainingType(), tr.getDate()));
//
//            logger.info("-------------------------------------------------");
//            logger.info("CREATING TRAINER #2");
//            logger.info("-------------------------------------------------");
//
//            TrainingType spec2 = new TrainingType();
//            spec2.setTrainingType("Python Programming");
//
//            Trainer trainer2 = new Trainer();
//            trainer2.setFirstName("Bob");
//            trainer2.setLastName("Smith");
//            trainer2.setSpecialization(spec2);
//
//            Trainer newTrainer2 = trainerProfile.trainerRegistration(trainer2);
//            logger.info("Trainer registered: {} {}", newTrainer2.getFirstName(), newTrainer2.getLastName());
//
//            trainerProfile.login(newTrainer2.getUserName(), newTrainer2.getPassword());
//
//            logger.info("-------------------------------------------------");
//            logger.info("CREATING TRAINEE #2");
//            logger.info("-------------------------------------------------");
//
//            Trainee trainee2 = new Trainee();
//            trainee2.setFirstName("Emma");
//            trainee2.setLastName("Watson");
//            trainee2.setDateOfBirth(LocalDate.of(1992, 2, 2));
//
//            Trainee newTrainee2 = traineeProfile.traineeRegistration(trainee2);
//            traineeProfile.login(newTrainee2.getUserName(), newTrainee2.getPassword());
//
//            logger.info("Trainee registered and logged in: {} {}", newTrainee2.getFirstName(), newTrainee2.getLastName());
//
//            logger.info("-------------------------------------------------");
//            logger.info("ADDING TRAINING FOR TRAINER #2");
//            logger.info("-------------------------------------------------");
//
//            Training training2 = new Training();
//            training2.setTrainingName("Python Basics");
//            training2.setTrainingType(spec2);
//            training2.setDate(LocalDateTime.of(2024, 7, 2, 10, 0));
//            training2.setDuration(60);
//            training2.setTrainer(newTrainer2);
//
//            trainerProfile.addTraining(newTrainer2.getUserName(), newTrainee2.getUserName(), training2);
//
//            traineeProfile.getTraineeTrainings(newTrainee2.getUserName(), null, null, null, null)
//                    .forEach(tr -> logger.info("Trainee's Training: {} | Type: {} | Date: {}",
//                            tr.getTrainingName(), tr.getTrainingType().getTrainingType(), tr.getDate()));
//
//            logger.info("-------------------------------------------------");
//            logger.info("LIST OF UNASSIGNED TRAINERS FOR TRAINEE #1");
//            logger.info("-------------------------------------------------");
//
//            List<Trainer> trainers = traineeProfile.getActiveTrainersNotAssignedToTrainee(newTrainee2.getUserName());
//
//            trainers.forEach(trainer5 ->
//                    logger.info("Unassigned Trainer: {} {}", trainer5.getFirstName(), trainer5.getLastName()));
//
//            traineeProfile.updatTraineeTrainersList(newTrainee.getUserName(), List.of(newTrainer.getUserName()));
//            traineeProfile.updatTraineeTrainersList(newTrainee.getUserName(), List.of(newTrainer2.getUserName(), newTrainer.getUserName()));
//
//            traineeProfile.getActiveTrainersNotAssignedToTrainee(newTrainee.getUserName())
//             .forEach(trainer6 ->
//                            logger.info("Unassigned Trainer after update: {} {}", trainer6.getFirstName(), trainer6.getUserName()));
//
//        } finally {
//            context.close();
//        }

    }
}
