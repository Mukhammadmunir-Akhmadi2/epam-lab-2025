package com.epam.infrastructure.controllers;

import com.epam.application.exceptions.ResourceNotFoundException;
import com.epam.application.exceptions.UnauthorizedAccess;
import com.epam.application.provider.AuthProviderService;
import com.epam.application.services.TraineeService;
import com.epam.application.services.TrainerQueryService;
import com.epam.application.services.TrainerService;
import com.epam.infrastructure.controllers.Impl.TraineeControllerImpl;
import com.epam.infrastructure.dtos.*;
import com.epam.infrastructure.mappers.TraineeFullMapper;
import com.epam.infrastructure.mappers.TraineeMapper;
import com.epam.infrastructure.mappers.TrainerMapper;
import com.epam.model.Trainee;
import com.epam.model.Trainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TraineeControllerImpl.class)
class TraineeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private TraineeService traineeService;

    @MockitoBean
    private TrainerService trainerService;

    @MockitoBean
    private TrainerQueryService trainerQueryService;

    @MockitoBean
    private AuthProviderService authProvider;

    @MockitoBean
    private TraineeMapper traineeMapper;

    @MockitoBean
    private TraineeFullMapper traineeFullMapper;

    @MockitoBean
    private TrainerMapper trainerMapper;

    @Test
    void register_ShouldReturnCreated() throws Exception {
        TraineeRegistrationRequest request = new TraineeRegistrationRequest();
        request.setFirstName("John");
        request.setLastName("Doe");

        AuthDto authDto = new AuthDto();
        authDto.setUsername("john.doe");
        authDto.setPassword("pass123");

        when(traineeMapper.toModel(request)).thenReturn(new Trainee());
        when(traineeService.createTrainee(any())).thenReturn(new Trainee());
        when(traineeMapper.toAuthDto(any())).thenReturn(authDto);

        mockMvc.perform(post("/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("john.doe"))
                .andExpect(jsonPath("$.password").value("pass123"));
    }
    @Test
    void updateProfile_ShouldReturnOk() throws Exception {
        TraineeDto traineeDto = new TraineeDto();
        traineeDto.setUsername("john.doe");
        traineeDto.setFirstName("John");
        traineeDto.setLastName("Doe");

        Trainee traineeModel = new Trainee();

        Trainee updatedTrainee = new Trainee();

        TraineeResponseDto responseDto = new TraineeResponseDto();
        responseDto.setUsername("john.doe");
        responseDto.setFirstName("John");
        responseDto.setLastName("Doe");

        doNothing().when(authProvider).ensureAuthenticated("john.doe");
        when(traineeMapper.toModel(traineeDto)).thenReturn(traineeModel);
        when(traineeService.updateTrainee(traineeModel)).thenReturn(updatedTrainee);
        when(traineeFullMapper.toTraineeResponseDto(updatedTrainee)).thenReturn(responseDto);

        mockMvc.perform(put("/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(traineeDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john.doe"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));

        verify(authProvider, times(1)).ensureAuthenticated("john.doe");
        verify(traineeMapper, times(1)).toModel(traineeDto);
        verify(traineeService, times(1)).updateTrainee(traineeModel);
        verify(traineeFullMapper, times(1)).toTraineeResponseDto(updatedTrainee);
    }


    @Test
    void getProfile_ShouldReturnOk() throws Exception {
        String username = "john.doe";
        Trainee trainee = new Trainee();
        TraineeResponseDto responseDto = new TraineeResponseDto();
        responseDto.setUsername(username);

        doNothing().when(authProvider).ensureAuthenticated(username);
        when(traineeService.getTraineeByUserName(username)).thenReturn(trainee);
        when(traineeFullMapper.toTraineeResponseDto(trainee)).thenReturn(responseDto);

        mockMvc.perform(get("/trainees/" + username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username));
    }

    @Test
    void deleteProfile_ShouldReturnNoContent() throws Exception {
        String username = "john.doe";
        doNothing().when(authProvider).ensureAuthenticated(username);
        doNothing().when(traineeService).deleteTrainee(username);

        mockMvc.perform(delete("/trainees/" + username))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateTraineeTrainersList_ShouldReturnOk() throws Exception {
        String username = "john.doe";
        List<String> trainers = List.of("trainer1", "trainer2");

        doNothing().when(authProvider).ensureAuthenticated(username);
        Trainer t1 = new Trainer();
        Trainer t2 = new Trainer();
        when(trainerService.getTrainerByUserName("trainer1")).thenReturn(t1);
        when(trainerService.getTrainerByUserName("trainer2")).thenReturn(t2);

        when(traineeService.updateTraineeTrainers(eq(username), anyList())).thenReturn(List.of(t1, t2));
        when(trainerMapper.toTrainerBriefDtoList(anyList())).thenReturn(List.of(new TrainerBriefDto(), new TrainerBriefDto()));

        mockMvc.perform(put("/trainees/" + username + "/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainers)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getUnassignedActiveTrainers_ShouldReturnOk() throws Exception {
        String username = "john.doe";
        doNothing().when(authProvider).ensureAuthenticated(username);

        Trainer t1 = new Trainer();
        Trainer t2 = new Trainer();
        when(trainerQueryService.getUnassignedActiveTrainers(username)).thenReturn(List.of(t1, t2));
        when(trainerMapper.toTrainerBriefDtoList(List.of(t1, t2))).thenReturn(List.of(new TrainerBriefDto(), new TrainerBriefDto()));

        mockMvc.perform(get("/trainees/{username}/unassigned-trainers", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }


    @Test
    void getProfile_Unauthorized_ShouldReturnUnauthorized() throws Exception {
        String username = "john.doe";
        doThrow(new UnauthorizedAccess("Not allowed")).when(authProvider).ensureAuthenticated(username);

        mockMvc.perform(get("/trainees/" + username))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Unauthorized Access"))
                .andExpect(jsonPath("$.detail").value("Not allowed"));
    }

    @Test
    void getProfile_NotFound_ShouldReturnNotFound() throws Exception {
        String username = "unknown";
        doNothing().when(authProvider).ensureAuthenticated(username);
        when(traineeService.getTraineeByUserName(username)).thenThrow(new ResourceNotFoundException("Trainee not found"));

        mockMvc.perform(get("/trainees/" + username))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value("Trainee not found"));
    }

    @Test
    void getUnassignedActiveTrainers_Unauthorized_ShouldReturnUnauthorized() throws Exception {
        String username = "john.doe";
        doThrow(new UnauthorizedAccess("Not allowed")).when(authProvider).ensureAuthenticated(username);

        mockMvc.perform(get("/trainees/{username}/unassigned-trainers", username))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Unauthorized Access"))
                .andExpect(jsonPath("$.detail").value("Not allowed"));
    }


    @Test
    void updateTraineeTrainersList_EmptyList_ShouldReturnBadRequest() throws Exception {
        String username = "john.doe";
        doNothing().when(authProvider).ensureAuthenticated(username);

        mockMvc.perform(put("/trainees/" + username + "/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isBadRequest());
    }
}
