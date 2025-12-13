package com.epam.infrastructure.security.controllers;

import com.epam.application.provider.AuthProviderService;
import com.epam.application.services.*;
import com.epam.infrastructure.dtos.*;
import com.epam.infrastructure.mappers.*;
import com.epam.model.Trainer;
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

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TraineeControllerSecurityTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TraineeService traineeService;
    @MockitoBean
    private TrainerService trainerService;
    @MockitoBean
    private TrainerQueryService trainerQueryService;
    @MockitoBean
    private AuthProviderService authProvider;
    @MockitoBean
    private RoleService roleService;
    @MockitoBean
    private TraineeMapper traineeMapper;
    @MockitoBean
    private TraineeFullMapper traineeFullMapper;
    @MockitoBean
    private TrainerMapper trainerMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void register_WithoutAuth_ShouldReturnCreated() throws Exception {
        TraineeRegistrationRequest request = new TraineeRegistrationRequest();
        request.setFirstName("John");
        request.setLastName("Doe");

        var authDto = new AuthDto();
        authDto.setUsername("user");
        authDto.setPassword("pass");
        when(roleService.getRole(any())).thenReturn(mock(com.epam.model.Role.class));
        when(traineeMapper.toModel((TraineeRegistrationRequest) any())).thenReturn(mock(com.epam.model.Trainee.class));
        when(traineeService.createTrainee(any())).thenReturn(mock(com.epam.model.Trainee.class));
        when(traineeMapper.toAuthDto(any())).thenReturn(authDto);

        mockMvc.perform(post("/trainees")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void updateProfile_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(put("/trainees/user")
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user", authorities = "TRAINEE")
    void updateProfile_WithAuth_ShouldReturnOk() throws Exception {
        TraineeDto dto = new TraineeDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setUsername("user");
        dto.setActive(true);

        doNothing().when(authProvider).validateCurrentUser("user");
        when(traineeMapper.toModel((TraineeRegistrationRequest) any())).thenReturn(mock(com.epam.model.Trainee.class));
        when(traineeService.updateTrainee(any())).thenReturn(mock(com.epam.model.Trainee.class));
        when(traineeFullMapper.toTraineeResponseDto(any())).thenReturn(new TraineeResponseDto());

        mockMvc.perform(put("/trainees/user")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "trainer", authorities = "TRAINER")
    void updateProfile_WithTrainer_ShouldReturnForbidden() throws Exception {
        TraineeDto dto = new TraineeDto();
        dto.setFirstName("John");

        mockMvc.perform(put("/trainees/user")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", authorities = "TRAINEE")
    void getProfile_WithAuth_ShouldReturnOk() throws Exception {
        doNothing().when(authProvider).validateCurrentUser("user");
        when(traineeService.getTraineeByUserName("user"))
                .thenReturn(mock(com.epam.model.Trainee.class));
        when(traineeFullMapper.toTraineeResponseDto(any()))
                .thenReturn(new TraineeResponseDto());

        mockMvc.perform(get("/trainees/user"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "trainer", authorities = "TRAINER")
    void getProfile_WithTrainer_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/trainees/user"))
                .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(username = "user", authorities = "TRAINEE")
    void deleteProfile_WithAuth_ShouldReturnNoContent() throws Exception {
        doNothing().when(authProvider).validateCurrentUser("user");
        doNothing().when(traineeService).deleteTrainee("user");

        mockMvc.perform(delete("/trainees/user"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "trainer", authorities = "TRAINER")
    void deleteProfile_WithTrainer_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/trainees/user"))
                .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(username = "user", authorities = "TRAINEE")
    void updateTraineeTrainers_WithAuth_ShouldReturnOk() throws Exception {
        doNothing().when(authProvider).validateCurrentUser("user");

        when(trainerService.getTrainerByUserName(anyString()))
                .thenReturn(mock(com.epam.model.Trainer.class));
        when(traineeService.updateTraineeTrainers(any(), any()))
                .thenReturn(List.of(mock(com.epam.model.Trainer.class)));
        when(trainerMapper.toTrainerBriefDtoList((Set<Trainer>) any()))
                .thenReturn(List.of(new TrainerBriefDto()));

        mockMvc.perform(put("/trainees/user/trainers")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(List.of("trainer1"))))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "trainer", authorities = "TRAINER")
    void updateTraineeTrainers_WithTrainer_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(put("/trainees/user/trainers")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(List.of("trainer1"))))
                .andExpect(status().isForbidden());
    }

}