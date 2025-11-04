package com.epam;

import com.epam.application.services.TraineeService;
import com.epam.application.services.TrainerService;
import com.epam.application.services.TrainingService;
import com.epam.infrastructure.config.AppConfig;
import com.epam.model.Trainee;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;

public class App {
    public static void main( String[] args ) {
        // for testing purposes
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        try {
            TrainingService trainingService = context.getBean(TrainingService.class);
            TraineeService traineeService = context.getBean(TraineeService.class);
            TrainerService trainerService = context.getBean(TrainerService.class);

            System.out.println("\n=== Existing Trainees ===");
            traineeService.getAllTrainees().forEach(System.out::println);

            System.out.println("\n=== Existing Trainers ===");
            trainerService.getAllTrainers().forEach(System.out::println);

            System.out.println("\n=== Existing Trainings ===");
            trainingService.getAllTrainings().forEach(System.out::println);

            System.out.println("\n=== New Trainee Saved ===");
            Trainee newTrainee = new Trainee();
            newTrainee.setFirstName("John");
            newTrainee.setLastName("Doe");
            newTrainee.setDateOfBirth(LocalDate.of(1999, 6,6));
            newTrainee.setAddress("123 Main St, Anytown, USA");
            Trainee savedTrainee = traineeService.createTrainee(newTrainee);

            System.out.println(savedTrainee);

            System.out.println("\n=== Remove trainee ===");
            traineeService.deleteTrainee(savedTrainee.getUserId().toString());

            System.out.println("\n=== Trainees after removal ===");
            traineeService.getAllTrainees().forEach(System.out::println);
        } finally {
            context.close();
        }

    }
}
