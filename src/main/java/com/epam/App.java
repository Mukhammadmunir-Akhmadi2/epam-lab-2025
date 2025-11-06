package com.epam;

import com.epam.application.repository.TraineeRepository;
import com.epam.application.repository.TrainerRepository;
import com.epam.application.repository.TrainingRepository;
import com.epam.application.services.TraineeService;
import com.epam.infrastructure.config.AppConfig;
import com.epam.model.Trainee;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;

public class App {
    public static void main( String[] args ) {
        // for testing purposes
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        try {
            TrainingRepository trainingRepository = context.getBean(TrainingRepository.class);
            TraineeRepository traineeRepository = context.getBean(TraineeRepository.class);
            TraineeService traineeService = context.getBean(TraineeService.class);
            TrainerRepository trainerRepository= context.getBean(TrainerRepository.class);

            System.out.println("\n=== Existing Trainees ===");
            traineeRepository.findAll().forEach(System.out::println);

            System.out.println("\n=== Existing Trainers ===");
            trainerRepository.findAll().forEach(System.out::println);

            System.out.println("\n=== Existing Trainings ===");
            trainingRepository.findAll().forEach(System.out::println);

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
            traineeRepository.findAll().forEach(System.out::println);
        } finally {
            context.close();
        }

    }
}
