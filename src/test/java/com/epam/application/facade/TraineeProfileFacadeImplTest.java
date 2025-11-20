package com.epam.application.facade;

import com.epam.application.exceptions.UnauthorizedAccess;
import com.epam.application.facade.impl.TraineeProfileFacadeImpl;
import com.epam.application.provider.AuthProviderService;
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
    private BaseUserAuthService authService;
    @Mock
    private TrainerService trainerService;
    @Mock
    private TrainerQueryService trainerQueryService;
    @Mock
    private TrainingQueryService trainingQueryService;
    @Mock
    private AuthProviderService authProviderService;

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

    private void mockAuthenticated() {
        when(authProviderService.isAuthenticated("trainee1")).thenReturn(true);
    }

    private void mockNotAuthenticated() {
        when(authProviderService.isAuthenticated("trainee1")).thenReturn(false);
    }

    @Test
    void getTraineeProfile_shouldReturnTrainee_whenAuthenticated() {
        mockAuthenticated();
        when(traineeService.getTraineeByUserName("trainee1")).thenReturn(trainee);

        Trainee result = facade.getTraineeProfile("trainee1");

        assertNotNull(result);
        assertEquals("trainee1", result.getUserName());
    }

    @Test
    void registerTrainee_shouldCallService() {
        when(traineeService.createTrainee(trainee)).thenReturn(trainee);

        Trainee result = facade.registerTrainee(trainee);

        assertEquals(trainee, result);
        verify(traineeService).createTrainee(trainee);
    }

    @Test
    void login_shouldAuthenticateTrainee() {
        when(authService.authenticateUser("trainee1", "password123"))
                .thenReturn("Authenticated successfully");

        String msg = facade.login("trainee1", "password123");

        assertTrue(msg.contains("successfully"));
    }

    @Test
    void updateTraineeProfile_shouldCallServiceWhenAuthenticated() {
        mockAuthenticated();
        when(traineeService.updateTrainee(trainee)).thenReturn(trainee);

        Trainee result = facade.updateTraineeProfile(trainee);

        assertEquals(trainee, result);
        verify(traineeService).updateTrainee(trainee);
    }

    @Test
    void getTraineeProfile_shouldThrowIfNotAuthenticated() {
        mockNotAuthenticated();
        assertThrows(UnauthorizedAccess.class,
                () -> facade.getTraineeProfile("trainee1"));
    }

    @Test
    void deleteTraineeProfile_shouldCallServiceWhenAuthenticated() {
        mockAuthenticated();

        facade.deleteTraineeProfile("trainee1");

        verify(traineeService).deleteTrainee("trainee1");
    }

    @Test
    void toggleActive_shouldCallAuthServiceWhenAuthenticated() {
        mockAuthenticated();
        when(authService.toggleActive("trainee1")).thenReturn(true);

        boolean result = facade.toggleActive("trainee1");

        assertTrue(result);
        verify(authService).toggleActive("trainee1");
    }

    @Test
    void changePassword_shouldCallAuthServiceWhenAuthenticated() {
        mockAuthenticated();

        facade.changePassword("trainee1", "oldpass", "newpass123");

        verify(authService).changePassword("trainee1", "oldpass", "newpass123");
    }

    @Test
    void updatTraineeTrainersList_shouldCallServiceWhenAuthenticated() {
        mockAuthenticated();

        when(trainerService.getTrainerByUserName("trainer1")).thenReturn(trainer);

        List<Trainer> result = facade.updatTraineeTrainersList("trainee1", List.of("trainer1"));

        assertEquals(1, result.size());
        verify(traineeService).updateTraineeTrainers("trainee1", List.of(trainer));
    }

    @Test
    void getActiveTrainersNotAssignedToTrainee_shouldReturnList() {
        mockAuthenticated();
        when(trainerQueryService.getUnassignedTrainers("trainee1"))
                .thenReturn(List.of(trainer));

        List<Trainer> result = facade.getActiveTrainersNotAssignedToTrainee("trainee1");

        assertEquals(1, result.size());
        verify(trainerQueryService).getUnassignedTrainers("trainee1");
    }

    @Test
    void getTraineeTrainings_shouldReturnList() {
        mockAuthenticated();

        when(trainingQueryService.getTraineeTrainings("trainee1", null, null, null, null))
                .thenReturn(List.of(training));

        List<Training> result =
                facade.getTraineeTrainings("trainee1", null, null, null, null);

        assertEquals(1, result.size());
        verify(trainingQueryService)
                .getTraineeTrainings("trainee1", null, null, null, null);
    }

    @Test
    void unauthorizedAccessMethods_shouldThrowIfNotAuthenticated() {
        mockNotAuthenticated();

        assertThrows(UnauthorizedAccess.class,
                () -> facade.updateTraineeProfile(trainee));
        assertThrows(UnauthorizedAccess.class,
                () -> facade.deleteTraineeProfile("trainee1"));
        assertThrows(UnauthorizedAccess.class,
                () -> facade.toggleActive("trainee1"));
        assertThrows(UnauthorizedAccess.class,
                () -> facade.changePassword("trainee1", "o", "n"));
        assertThrows(UnauthorizedAccess.class,
                () -> facade.updatTraineeTrainersList("trainee1", List.of("trainer1")));
        assertThrows(UnauthorizedAccess.class,
                () -> facade.getActiveTrainersNotAssignedToTrainee("trainee1"));
        assertThrows(UnauthorizedAccess.class,
                () -> facade.getTraineeTrainings("trainee1",
                        LocalDate.now(), LocalDate.now().plusDays(1), null, null));
    }
}