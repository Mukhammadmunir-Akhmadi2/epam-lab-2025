package com.epam.infrastructure.security.controllers;

import com.epam.application.provider.AuthProviderService;
import com.epam.application.services.BaseUserAuthService;
import com.epam.infrastructure.dtos.AuthDto;
import com.epam.infrastructure.dtos.ChangePasswordRequest;
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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerSecurityTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BaseUserAuthService baseUserAuthService;

    @MockitoBean
    private AuthProviderService authProvider;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void login_WithValidBody_ShouldReturnOk() throws Exception {
        AuthDto loginRequest = new AuthDto();
        loginRequest.setUsername("user");
        loginRequest.setPassword("password");

        when(baseUserAuthService.authenticateUser("user", "password")).thenReturn("valid-token");

        mockMvc.perform(post("/users/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("valid-token"));
    }

    @Test
    void login_WithoutBody_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/users/login")
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changePassword_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(put("/users/user/password")
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user")
    void changePassword_WithAuth_ShouldReturnOk() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("old");
        request.setNewPassword("new123");

        doNothing().when(authProvider).validateCurrentUser("user");
        doNothing().when(baseUserAuthService).changePassword("user", "old", "new123");

        mockMvc.perform(put("/users/user/password")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user")
    void toggleActive_WithAuth_ShouldReturnOk() throws Exception {
        mockMvc.perform(patch("/users/user/active"))
                .andExpect(status().isOk());
    }
    @Test
    void toggleActive_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(patch("/users/user/active"))
                .andExpect(status().isUnauthorized());
    }
}