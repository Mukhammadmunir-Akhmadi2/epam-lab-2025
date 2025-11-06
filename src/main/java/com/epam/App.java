package com.epam;

import com.epam.application.repository.TraineeRepository;
import com.epam.application.repository.TrainerRepository;
import com.epam.application.repository.TrainingRepository;
import com.epam.application.services.TraineeService;
import com.epam.infrastructure.config.AppConfig;
import com.epam.model.Trainee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main( String[] args ) {
        // for testing purposes
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        try {
            TrainingRepository trainingRepository = context.getBean(TrainingRepository.class);
            TraineeRepository traineeRepository = context.getBean(TraineeRepository.class);
            TraineeService traineeService = context.getBean(TraineeService.class);
            TrainerRepository trainerRepository= context.getBean(TrainerRepository.class);


            logger.info("Listing all existing trainees:");
            traineeRepository.findAll().forEach(t -> logger.info("{}", t));

            logger.info("Listing all existing trainers:");
            trainerRepository.findAll().forEach(t -> logger.info("{}", t));


            logger.info("Listing all existing trainings:");
            trainingRepository.findAll().forEach(t -> logger.info("{}", t));


            logger.info("Saving new trainee:");
            Trainee newTrainee = new Trainee();
            newTrainee.setFirstName("John");
            newTrainee.setLastName("Doe");
            newTrainee.setDateOfBirth(LocalDate.of(1999, 6,6));
            newTrainee.setAddress("123 Main St, Anytown, USA");
            Trainee savedTrainee = traineeService.createTrainee(newTrainee);

            logger.info("{}", savedTrainee);

            logger.info("Removing trainee:");
            traineeService.deleteTrainee(savedTrainee.getUserId());

            logger.info("Listing all existing trainees after removal:");
            logger.info("{}", traineeRepository.findAll());
        } finally {
            context.close();
        }

    }
}
