package com.epam.application.facade;

import com.epam.application.exceptions.InvalidCredentialException;
import com.epam.application.exceptions.UnauthorizedAccess;
import com.epam.application.facade.impl.TraineeProfileFacadeImpl;
import com.epam.application.services.*;
import com.epam.model.Trainee;
import com.epam.model.Trainer;
import com.epam.model.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TraineeProfileFacadeImplTest {

    @Mock
    private TraineeService traineeService;
    @Mock
    private TraineeAuthService traineeAuthService;
    @Mock
    private TrainerService trainerService;
    @Mock
    private TrainerQueryService trainerQueryService;
    @Mock
    private TrainingQueryService trainingQueryService;

    @InjectMocks
    private TraineeProfileFacadeImpl facade;

    private Trainee trainee;
    private Trainer trainer;
    private Training training;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
        trainee.setUserName("trainee1");
        trainee.setPassword("password123");

        trainer = new Trainer();
        trainer.setUserName("trainer1");

        training = new Training();
    }

    @Test
    void getTraineeProfile_shouldReturnTrainee_whenAuthenticated() {
        Trainee trainee = new Trainee();
        trainee.setUserName("trainee1");
        trainee.setPassword("password123");

        when(traineeService.getTraineeByUserName("trainee1")).thenReturn(trainee);
        facade.login("trainee1", "password123");

        Trainee result = facade.getTraineeProfile("trainee1");

        assertNotNull(result);
        assertEquals("trainee1", result.getUserName());
        verify(traineeService, times(2)).getTraineeByUserName("trainee1");
    }

    @Test
    void traineeRegistration_shouldCallService() {
        when(traineeService.createTrainee(trainee)).thenReturn(trainee);

        Trainee result = facade.traineeRegistration(trainee);

        assertNotNull(result);
        verify(traineeService, times(1)).createTrainee(trainee);
    }

    @Test
    void login_shouldAuthenticateTrainee() {
        when(traineeService.getTraineeByUserName("trainee1")).thenReturn(trainee);

        String msg = facade.login("trainee1", "password123");

        assertTrue(msg.contains("authenticated successfully"));
    }

    @Test
    void login_shouldThrowIfPasswordIncorrect() {
        when(traineeService.getTraineeByUserName("trainee1")).thenReturn(trainee);

        assertThrows(InvalidCredentialException.class, () ->
                facade.login("trainee1", "wrongpass"));
    }

    @Test
    void updateTraineeProfile_shouldCallServiceWhenAuthenticated() {
        when(traineeService.getTraineeByUserName("trainee1")).thenReturn(trainee);
        facade.login("trainee1", "password123");

        when(traineeService.updateTrainee(trainee)).thenReturn(trainee);

        Trainee result = facade.updateTraineeProfile(trainee);

        assertEquals(trainee, result);
        verify(traineeService, times(1)).updateTrainee(trainee);
    }

    @Test
    void getTraineeProfile_shouldThrowIfNotAuthenticated() {
        assertThrows(UnauthorizedAccess.class, () ->
                facade.getTraineeProfile("trainee1"));
    }

    @Test
    void deleteTraineeProfile_shouldCallServiceWhenAuthenticated() {
        when(traineeService.getTraineeByUserName("trainee1")).thenReturn(trainee);
        facade.login("trainee1", "password123");

        facade.deleteTraineeProfile("trainee1");

        verify(traineeService, times(1)).deleteTrainee("trainee1");
    }

    @Test
    void toggleActive_shouldCallAuthServiceWhenAuthenticated() {
        when(traineeService.getTraineeByUserName("trainee1")).thenReturn(trainee);
        facade.login("trainee1", "password123");

        when(traineeAuthService.toggleActive("trainee1")).thenReturn("deactivated");

        String status = facade.toggleActive("trainee1");

        assertEquals("deactivated", status);
        verify(traineeAuthService, times(1)).toggleActive("trainee1");
    }

    @Test
    void changePassword_shouldCallAuthServiceWhenAuthenticated() {
        when(traineeService.getTraineeByUserName("trainee1")).thenReturn(trainee);
        facade.login("trainee1", "password123");

        facade.changePassword("trainee1", "oldpass", "newpass123");

        verify(traineeAuthService, times(1)).changePassword("trainee1", "oldpass", "newpass123");
    }

    @Test
    void updatTraineeTrainersList_shouldCallServiceWhenAuthenticated() {
        when(traineeService.getTraineeByUserName("trainee1")).thenReturn(trainee);
        facade.login("trainee1", "password123");

        when(trainerService.getTrainerByUserName("trainer1")).thenReturn(trainer);

        List<Trainer> result = facade.updatTraineeTrainersList("trainee1", List.of("trainer1"));

        assertEquals(1, result.size());
        verify(traineeService, times(1)).updateTraineeTrainers("trainee1", List.of(trainer));
    }

    @Test
    void getActiveTrainersNotAssignedToTrainee_shouldReturnList() {
        when(traineeService.getTraineeByUserName("trainee1")).thenReturn(trainee);
        facade.login("trainee1", "password123");

        when(trainerQueryService.getUnassignedTrainers("trainee1")).thenReturn(List.of(trainer));

        List<Trainer> result = facade.getActiveTrainersNotAssignedToTrainee("trainee1");

        assertEquals(1, result.size());
        verify(trainerQueryService, times(1)).getUnassignedTrainers("trainee1");
    }

    @Test
    void getTraineeTrainings_shouldReturnList() {
        when(traineeService.getTraineeByUserName("trainee1")).thenReturn(trainee);
        facade.login("trainee1", "password123");

        when(trainingQueryService.getTraineeTrainings("trainee1", null, null, null, null))
                .thenReturn(List.of(training));

        List<Training> result = facade.getTraineeTrainings("trainee1", null, null, null, null);

        assertEquals(1, result.size());
        verify(trainingQueryService, times(1))
                .getTraineeTrainings("trainee1", null, null, null, null);
    }
    @Test
    void updateTraineeProfile_shouldThrowIfNotAuthenticated() {
        assertThrows(UnauthorizedAccess.class, () -> facade.updateTraineeProfile(trainee));
    }

    @Test
    void deleteTraineeProfile_shouldThrowIfNotAuthenticated() {
        assertThrows(UnauthorizedAccess.class, () -> facade.deleteTraineeProfile("trainee1"));
    }

    @Test
    void toggleActive_shouldThrowIfNotAuthenticated() {
        assertThrows(UnauthorizedAccess.class, () -> facade.toggleActive("trainee1"));
    }

    @Test
    void changePassword_shouldThrowIfNotAuthenticated() {
        assertThrows(UnauthorizedAccess.class, () ->
                facade.changePassword("trainee1", "oldPass", "newPass123"));
    }

    @Test
    void updatTraineeTrainersList_shouldThrowIfNotAuthenticated() {
        assertThrows(UnauthorizedAccess.class, () ->
                facade.updatTraineeTrainersList("trainee1", List.of("trainer1")));
    }

    @Test
    void getActiveTrainersNotAssignedToTrainee_shouldThrowIfNotAuthenticated() {
        assertThrows(UnauthorizedAccess.class, () ->
                facade.getActiveTrainersNotAssignedToTrainee("trainee1"));
    }

    @Test
    void getTraineeTrainings_shouldThrowIfNotAuthenticated() {
        assertThrows(UnauthorizedAccess.class, () ->
                facade.getTraineeTrainings("trainee1", LocalDate.now(), LocalDate.now().plusDays(1), null, null));
    }
}
