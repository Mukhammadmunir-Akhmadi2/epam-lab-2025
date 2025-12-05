package com.epam.infrastructure.controllers;

import com.epam.application.exceptions.ResourceNotFoundException;
import com.epam.application.exceptions.UnauthorizedAccess;
import com.epam.application.provider.AuthProviderService;
import com.epam.application.services.*;
import com.epam.infrastructure.controllers.Impl.TrainingControllerImpl;
import com.epam.infrastructure.dtos.*;
import com.epam.infrastructure.enums.TrainingTypeEnum;
import com.epam.infrastructure.mappers.TrainingMapper;
import com.epam.model.Trainee;
import com.epam.model.Trainer;
import com.epam.model.Training;
import com.epam.model.TrainingType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainingControllerImpl.class)
class TrainingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean private TrainingService trainingService;
    @MockitoBean private TrainerService trainerService;
    @MockitoBean private TraineeService traineeService;
    @MockitoBean private TrainingTypeService trainingTypeService;
    @MockitoBean private TrainingQueryService trainingQueryService;
    @MockitoBean private AuthProviderService authProviderService;
    @MockitoBean private TrainingMapper trainingMapper;

    @Test
    void addTraining_ShouldReturnOk() throws Exception {

        String traineeUsername = "john.trainee";
        String trainerUsername = "jane.trainer";

        TrainingDto request = new TrainingDto();
        request.setName("Test");
        request.setDate("2025-10-10 10:00");
        request.setDuration(60);
        request.setType(TrainingTypeEnum.YOGA);

        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        TrainingType trainingType = new TrainingType();
        Training training = new Training();

        doNothing().when(authProviderService)
                .ensureAuthenticated(eq(traineeUsername));

        when(traineeService.getTraineeByUserName(traineeUsername)).thenReturn(trainee);
        when(trainerService.getTrainerByUserName(trainerUsername)).thenReturn(trainer);
        when(trainingTypeService.getTrainingType(request.getType())).thenReturn(trainingType);
        when(trainingMapper.toModel(trainee, trainer, trainingType, request)).thenReturn(training);
        when(trainingService.createTraining(training)).thenReturn(training);

        mockMvc.perform(post("/trainings/trainee/{username}/trainer/{trainerUsername}",
                        traineeUsername, trainerUsername)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void getTraineeTrainings_ShouldReturnOk() throws Exception {
        String username = "john.trainee";

        doNothing().when(authProviderService).ensureAuthenticated(username);
        when(trainingQueryService.getTraineeTrainings(username, null, null, null, null))
                .thenReturn(List.of(new Training()));
        when(trainingMapper.toTrainerTrainingDtoList(anyList()))
                .thenReturn(List.of(new TrainerTrainingDto()));

        mockMvc.perform(get("/trainings/trainee/{username}", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getTrainerTrainings_ShouldReturnOk() throws Exception {
        String username = "jane.trainer";

        doNothing().when(authProviderService).ensureAuthenticated(username);
        when(trainingQueryService.getTrainerTrainings(username, null, null, null))
                .thenReturn(List.of(new Training()));
        when(trainingMapper.toTraineeTrainingDtoList(anyList()))
                .thenReturn(List.of(new TraineeTrainingDto()));

        mockMvc.perform(get("/trainings/trainer/{username}", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void addTraining_Unauthorized_ShouldReturnUnauthorized() throws Exception {

        String traineeUsername = "john.trainee";
        String trainerUsername = "jane.trainer";

        TrainingDto request = new TrainingDto();
        request.setType(TrainingTypeEnum.YOGA);
        request.setDate("2025-11-25 10:15");
        request.setName("Morning Yoga");
        request.setDuration(60);

        doThrow(new UnauthorizedAccess("Not allowed"))
                .when(authProviderService).ensureAuthenticated(traineeUsername);

        mockMvc.perform(post("/trainings/trainee/{username}/trainer/{trainerUsername}",
                        traineeUsername, trainerUsername)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Unauthorized Access"))
                .andExpect(jsonPath("$.detail").value("Not allowed"));
    }

    @Test
    void addTraining_TraineeNotFound_ShouldReturnNotFound() throws Exception {

        String traineeUsername = "unknown.trainee";
        String trainerUsername = "jane.trainer";

        TrainingDto request = new TrainingDto();
        request.setType(TrainingTypeEnum.YOGA);
        request.setDate("2025-11-25 10:15");
        request.setName("Morning Yoga");
        request.setDuration(60);

        doNothing().when(authProviderService).ensureAuthenticated(traineeUsername);
        when(traineeService.getTraineeByUserName(traineeUsername))
                .thenThrow(new ResourceNotFoundException("Trainee not found"));

        mockMvc.perform(post("/trainings/trainee/{username}/trainer/{trainerUsername}",
                        traineeUsername, trainerUsername)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value("Trainee not found"));
    }

    @Test
    void addTraining_ValidationFailure_ShouldReturnBadRequest() throws Exception {

        String traineeUsername = "john.trainee";
        String trainerUsername = "jane.trainer";

        TrainingDto invalidRequest = new TrainingDto(); // missing required fields

        mockMvc.perform(post("/trainings/trainee/{username}/trainer/{trainerUsername}",
                        traineeUsername, trainerUsername)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
