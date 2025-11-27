package com.epam.infrastructure.controllers;

import com.epam.application.exceptions.InvalidCredentialException;
import com.epam.application.exceptions.ResourceNotFoundException;
import com.epam.application.exceptions.UnauthorizedAccess;
import com.epam.application.provider.AuthProviderService;
import com.epam.application.services.BaseUserAuthService;
import com.epam.infrastructure.controllers.Impl.AuthControllerImpl;
import com.epam.infrastructure.dtos.AuthDto;
import com.epam.infrastructure.dtos.ChangePasswordRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthControllerImpl.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private BaseUserAuthService baseUserAuthService;

    @MockitoBean
    private AuthProviderService authProvider;

    @Test
    void login_ShouldReturnOk() throws Exception {
        AuthDto request = new AuthDto();
        request.setUsername("user");
        request.setPassword("pass");

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(baseUserAuthService).authenticateUser("user", "pass");
    }

    @Test
    void changePassword_ShouldReturnOk() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("old");
        request.setNewPassword("new123");

        doNothing().when(authProvider).ensureAuthenticated("user");

        mockMvc.perform(put("/users/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(baseUserAuthService).changePassword("user", "old", "new123");
    }

    @Test
    void toggleActive_ShouldReturnOk() throws Exception {
        doNothing().when(authProvider).ensureAuthenticated("user");

        mockMvc.perform(patch("/users/user/active"))
                .andExpect(status().isOk());

        verify(baseUserAuthService).toggleActive("user");
    }

    @Test
    void login_InvalidCredentials_ShouldReturnBadRequest() throws Exception {
        AuthDto request = new AuthDto();
        request.setUsername("user");
        request.setPassword("wrong");

        doThrow(new InvalidCredentialException("Invalid password"))
                .when(baseUserAuthService).authenticateUser("user", "wrong");

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Credentials"))
                .andExpect(jsonPath("$.detail").value("Invalid password"));
    }

    @Test
    void changePassword_Unauthorized_ShouldReturnUnauthorized() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("old");
        request.setNewPassword("new123");

        doThrow(new UnauthorizedAccess("Not allowed"))
                .when(authProvider).ensureAuthenticated("user");

        mockMvc.perform(put("/users/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Unauthorized Access"))
                .andExpect(jsonPath("$.detail").value("Not allowed"));
    }

    @Test
    void toggleActive_UserNotFound_ShouldReturnNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("User not found"))
                .when(baseUserAuthService).toggleActive("unknown");

        mockMvc.perform(patch("/users/unknown/active"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.detail").value("User not found"));
    }

    @Test
    void changePassword_InvalidRequest_ShouldReturnBadRequest() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("");
        request.setNewPassword("new");

        mockMvc.perform(put("/users/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Error"));
    }
}
