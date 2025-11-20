package com.epam.application.facade;

import com.epam.application.exceptions.UnauthorizedAccess;
import com.epam.application.facade.impl.TrainerProfileFacadeImpl;
import com.epam.application.provider.AuthProviderService;
import com.epam.application.services.*;
import com.epam.infrastructure.enums.TrainingTypeEnum;
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
    private BaseUserAuthService authService;
    @Mock
    private TrainingService trainingService;
    @Mock
    private TrainingQueryService trainingQueryService;
    @Mock
    private AuthProviderService authProviderService;
    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainingTypeService trainingTypeService;

    @InjectMocks
    private TrainerProfileFacadeImpl facade;

    private Trainer trainer;
    private Trainee trainee;
    private Training training;
    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        trainer = new Trainer();
        trainer.setUserName("trainer1");

        trainee = new Trainee();
        trainee.setUserName("trainee1");

        trainingType = new TrainingType();
        trainingType.setTrainingType(TrainingTypeEnum.CARDIO);

        trainer.setSpecialization(trainingType);

        training = new Training();
        training.setTrainingType(trainingType);
    }

    private void mockAuthenticated() {
        when(authProviderService.isAuthenticated("trainer1")).thenReturn(true);
    }

    private void mockNotAuthenticated() {
        when(authProviderService.isAuthenticated("trainer1")).thenReturn(false);
    }

    // ======================
    // Registration
    // ======================
    @Test
    void registerTrainer_shouldCallService() {
        when(trainerService.createTrainer(trainer)).thenReturn(trainer);

        Trainer result = facade.registerTrainer(trainer);

        assertEquals(trainer, result);
        verify(trainerService).createTrainer(trainer);
    }

    // ======================
    // Login
    // ======================
    @Test
    void login_shouldAuthenticateTrainer() {
        when(authService.authenticateUser("trainer1", "password123"))
                .thenReturn("Authenticated");

        String msg = facade.login("trainer1", "password123");

        assertEquals("Authenticated", msg);
    }

    // ======================
    // Toggle Active
    // ======================
    @Test
    void toggleActive_shouldCallAuthServiceWhenAuthenticated() {
        mockAuthenticated();
        when(authService.toggleActive("trainer1")).thenReturn(true);

        boolean result = facade.toggleActive("trainer1");

        assertTrue(result);
        verify(authService).toggleActive("trainer1");
    }

    // ======================
    // Change Password
    // ======================
    @Test
    void changePassword_shouldCallAuthServiceWhenAuthenticated() {
        mockAuthenticated();

        facade.changePassword("trainer1", "oldpass", "newpass123");

        verify(authService).changePassword("trainer1", "oldpass", "newpass123");
    }

    // ======================
    // Add Training
    // ======================
    @Test
    void addTraining_shouldCallServicesWhenAuthenticated() {
        mockAuthenticated();

        when(trainerService.getTrainerByUserName("trainer1")).thenReturn(trainer);
        when(traineeService.getTraineeByUserName("trainee1")).thenReturn(trainee);
        when(trainingTypeService.getTrainingType(TrainingTypeEnum.CARDIO)).thenReturn(trainingType);

        facade.addTraining("trainer1", "trainee1", training);

        assertEquals(trainer, training.getTrainer());
        assertEquals(trainee, training.getTrainee());
        assertEquals(trainingType, training.getTrainingType());

        verify(trainingService).createTraining(training);
    }

    // ======================
    // Get Trainer Trainings
    // ======================
    @Test
    void getTrainerTrainings_shouldReturnListWhenAuthenticated() {
        mockAuthenticated();

        when(trainingQueryService.getTrainerTrainings("trainer1", null, null, null))
                .thenReturn(List.of(training));

        List<Training> result = facade.getTrainerTrainings("trainer1", null, null, null);

        assertEquals(1, result.size());
        verify(trainingQueryService).getTrainerTrainings("trainer1", null, null, null);
    }

    // ======================
    // Authentication failures
    // ======================
    @Test
    void unauthorized_shouldThrowWhenNotAuthenticated() {
        mockNotAuthenticated();

        assertThrows(UnauthorizedAccess.class, () ->
                facade.getTrainerProfile("trainer1"));

        assertThrows(UnauthorizedAccess.class, () ->
                facade.updateTrainerProfile(trainer));

        assertThrows(UnauthorizedAccess.class, () ->
                facade.toggleActive("trainer1"));

        assertThrows(UnauthorizedAccess.class, () ->
                facade.changePassword("trainer1", "old", "newpass123"));

        assertThrows(UnauthorizedAccess.class, () ->
                facade.addTraining("trainer1", "trainee1", training));

        assertThrows(UnauthorizedAccess.class, () ->
                facade.getTrainerTrainings("trainer1", null, null, null));
    }

    // ======================
    // Get Profile
    // ======================
    @Test
    void getTrainerProfile_shouldReturnTrainerWhenAuthenticated() {
        mockAuthenticated();
        when(trainerService.getTrainerByUserName("trainer1")).thenReturn(trainer);

        Trainer result = facade.getTrainerProfile("trainer1");

        assertEquals(trainer, result);
    }

    // ======================
    // Update Profile
    // ======================
    @Test
    void updateTrainerProfile_shouldCallServicesWhenAuthenticated() {
        mockAuthenticated();
        when(trainerService.updateTrainer(trainer)).thenReturn(trainer);

        Trainer result = facade.updateTrainerProfile(trainer);

        assertEquals(trainer, result);
        verify(trainerService).updateTrainer(trainer);
    }
}
