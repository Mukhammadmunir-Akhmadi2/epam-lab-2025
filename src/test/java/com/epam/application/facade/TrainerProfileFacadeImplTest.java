package com.epam.application.facade;

import com.epam.application.exceptions.InvalidCredentialException;
import com.epam.application.exceptions.UnauthorizedAccess;
import com.epam.application.facade.impl.TrainerProfileFacadeImpl;
import com.epam.application.services.*;
import com.epam.model.Trainee;
import com.epam.model.Trainer;
import com.epam.model.Training;
import com.epam.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerProfileFacadeImplTest {

    @Mock
    private TrainerService trainerService;
    @Mock
    private TrainerAuthService trainerAuthService;
    @Mock
    private TrainingService trainingService;
    @Mock
    private TrainingTypeService trainingTypeService;
    @Mock
    private TrainingQueryService trainingQueryService;
    @Mock
    private TraineeService traineeService;

    @InjectMocks
    private TrainerProfileFacadeImpl facade;

    private Trainer trainer;
    private Training training;
    private Trainee trainee;
    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        trainer = new Trainer();
        trainer.setUserName("trainer1");
        trainer.setPassword("password123");

        TrainingType specialization = new TrainingType();
        specialization.setTrainingType("CARDIO");
        trainer.setSpecialization(specialization);

        trainee = new Trainee();
        trainee.setUserName("trainee1");

        trainingType = new TrainingType();
        trainingType.setTrainingType("CARDIO");

        training = new Training();
        training.setTrainingType(trainingType);
    }

    @Test
    void trainerRegistration_shouldCallService() {
        when(trainingTypeService.findOrCreate("CARDIO")).thenReturn(trainingType);
        when(trainerService.createTrainer(trainer)).thenReturn(trainer);

        Trainer result = facade.trainerRegistration(trainer);

        assertEquals(trainer, result);
        verify(trainingTypeService).findOrCreate("CARDIO");
        verify(trainerService).createTrainer(trainer);
    }

    @Test
    void login_shouldAuthenticateTrainer() {
        when(trainerService.getTrainerByUserName("trainer1")).thenReturn(trainer);

        String msg = facade.login("trainer1", "password123");
        assertTrue(msg.contains("authenticated successfully"));
    }

    @Test
    void login_shouldThrowIfPasswordIncorrect() {
        when(trainerService.getTrainerByUserName("trainer1")).thenReturn(trainer);

        assertThrows(InvalidCredentialException.class, () ->
                facade.login("trainer1", "wrongpass"));
    }

    @Test
    void toggleActive_shouldCallAuthServiceWhenAuthenticated() {
        when(trainerService.getTrainerByUserName("trainer1")).thenReturn(trainer);
        facade.login("trainer1", "password123");

        when(trainerAuthService.toggleActive("trainer1")).thenReturn("deactivated");

        String status = facade.toggleActive("trainer1");

        assertEquals("deactivated", status);
        verify(trainerAuthService).toggleActive("trainer1");
    }

    @Test
    void changePassword_shouldCallAuthServiceWhenAuthenticated() {
        when(trainerService.getTrainerByUserName("trainer1")).thenReturn(trainer);
        facade.login("trainer1", "password123");

        facade.changePassword("trainer1", "oldpass", "newpass123");
        verify(trainerAuthService).changePassword("trainer1", "oldpass", "newpass123");
    }

    @Test
    void addTraining_shouldCallAllServicesWhenAuthenticated() {
        when(trainerService.getTrainerByUserName("trainer1")).thenReturn(trainer);
        when(traineeService.getTraineeByUserName("trainee1")).thenReturn(trainee);
        when(trainingTypeService.findOrCreate("CARDIO")).thenReturn(trainingType);

        facade.login("trainer1", "password123");
        facade.addTraining("trainer1", "trainee1", training);

        assertEquals(trainer, training.getTrainer());
        assertEquals(trainee, training.getTrainee());
        assertEquals(trainingType, training.getTrainingType());

        verify(trainingService).createTraining(training);
    }

    @Test
    void getTrainerTrainings_shouldReturnListWhenAuthenticated() {
        when(trainerService.getTrainerByUserName("trainer1")).thenReturn(trainer);
        facade.login("trainer1", "password123");

        when(trainingQueryService.getTrainerTrainings("trainer1", null, null, null))
                .thenReturn(List.of(training));

        List<Training> result = facade.getTrainerTrainings("trainer1", null, null, null);

        assertEquals(1, result.size());
        verify(trainingQueryService).getTrainerTrainings("trainer1", null, null, null);
    }

    @Test
    void isAuthenticated_shouldThrowIfNotLoggedIn() {
        assertThrows(UnauthorizedAccess.class, () ->
                facade.getTrainerProfile("trainer1"));
    }

    @Test
    void updateTrainerProfile_shouldCallServiceWhenAuthenticated() {
        when(trainerService.getTrainerByUserName("trainer1")).thenReturn(trainer);
        facade.login("trainer1", "password123");
        when(trainingTypeService.findOrCreate("CARDIO")).thenReturn(trainingType);
        when(trainerService.updateTrainer(trainer)).thenReturn(trainer);

        trainer.setSpecialization(trainingType);
        Trainer result = facade.updateTrainerProfile(trainer);

        assertEquals(trainer, result);
        verify(trainingTypeService).findOrCreate("CARDIO");
        verify(trainerService).updateTrainer(trainer);
    }

    @Test
    void getTrainerProfile_shouldReturnTrainerWhenAuthenticated() {
        when(trainerService.getTrainerByUserName("trainer1")).thenReturn(trainer);
        facade.login("trainer1", "password123");

        Trainer result = facade.getTrainerProfile("trainer1");
        assertEquals(trainer, result);
    }
}