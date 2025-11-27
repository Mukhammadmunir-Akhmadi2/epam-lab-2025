package com.epam.infrastructure.controllers.Impl;

import com.epam.application.provider.AuthProviderService;
import com.epam.application.services.BaseUserAuthService;
import com.epam.infrastructure.controllers.AuthController;
import com.epam.infrastructure.dtos.ChangePasswordRequest;
import com.epam.infrastructure.dtos.AuthDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {

    private final BaseUserAuthService baseUserAuthService;
    private final AuthProviderService authProvider;

    @Override
    public ResponseEntity<Void> login(AuthDto loginRequest) {
        baseUserAuthService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> changePassword(String username, ChangePasswordRequest request) {
        authProvider.ensureAuthenticated(username);
        baseUserAuthService.changePassword(username, request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> toggleActive(String username) {
        authProvider.ensureAuthenticated(username);
        baseUserAuthService.toggleActive(username);
        return ResponseEntity.ok().build();
    }
}
