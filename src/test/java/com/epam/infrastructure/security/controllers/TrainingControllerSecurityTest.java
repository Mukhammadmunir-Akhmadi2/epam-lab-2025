package com.epam.infrastructure.security.controllers;

import com.epam.application.provider.AuthProviderService;
import com.epam.application.services.*;
import com.epam.infrastructure.dtos.TrainingDto;
import com.epam.infrastructure.enums.TrainingTypeEnum;
import com.epam.infrastructure.mappers.TrainingMapper;
import com.epam.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TrainingControllerSecurityTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TrainingQueryService trainingQueryService;
    @MockitoBean
    private AuthProviderService authProviderService;
    @MockitoBean
    private TrainingService trainingService;
    @MockitoBean
    private TrainerService trainerService;
    @MockitoBean
    private TraineeService traineeService;
    @MockitoBean
    private TrainingTypeService trainingTypeService;
    @MockitoBean
    private TrainingMapper trainingMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void addTraining_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/trainings/trainee/user/trainer/trainer1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user", authorities = "TRAINER")
    void addTraining_WithTrainerRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/trainings/trainee/user/trainer/trainer1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", authorities = "TRAINEE")
    void addTraining_WithTraineeRole_ShouldReturnOk() throws Exception {

        TrainingDto dto = new TrainingDto();
        dto.setName("Morning Yoga");
        dto.setDuration(60);
        dto.setType(TrainingTypeEnum.YOGA);
        dto.setDate(String.valueOf(LocalDate.now()));

        when(authProviderService.isAuthenticated("user"))
                .thenReturn(true);

        when(traineeService.getTraineeByUserName("user"))
                .thenReturn(mock(Trainee.class));

        when(trainerService.getTrainerByUserName("trainer1"))
                .thenReturn(mock(Trainer.class));

        when(trainingTypeService.getTrainingType(any()))
                .thenReturn(mock(TrainingType.class));

        when(trainingMapper.toModel(any(), any(), any(), any()))
                .thenReturn(mock(Training.class));

        when(trainingService.createTraining(any()))
                .thenReturn(mock(Training.class));

        mockMvc.perform(post("/trainings/trainee/user/trainer/trainer1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "user", authorities = "TRAINEE")
    void getTraineeTrainings_WithTraineeRole_ShouldReturnOk() throws Exception {
        doNothing().when(authProviderService).validateCurrentUser("user");
        when(trainingQueryService.getTraineeTrainings(any(), any(), any(), any(), any()))
                .thenReturn(List.of());
        when(trainingMapper.toTrainerTrainingDtoList(any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/trainings/trainee/user"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", authorities = "TRAINER")
    void getTraineeTrainings_WithTrainerRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/trainings/trainee/user"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "trainer1", authorities = "TRAINER")
    void getTrainerTrainings_WithTrainerRole_ShouldReturnOk() throws Exception {
        doNothing().when(authProviderService).validateCurrentUser("trainer1");
        when(trainingQueryService.getTrainerTrainings(any(), any(), any(), any()))
                .thenReturn(List.of());
        when(trainingMapper.toTraineeTrainingDtoList(any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/trainings/trainer/trainer1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", authorities = "TRAINEE")
    void getTrainerTrainings_WithTraineeRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/trainings/trainer/trainer1"))
                .andExpect(status().isForbidden());
    }
}
