package com.epam.infrastructure.security.auth;

import com.epam.infrastructure.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InternalTokenService {

    private final JwtService jwtService;

    public String issueServiceToken() {
        UserDetails system = User.builder()
                .username("workload-client")
                .password("")
                .authorities("SYSTEM")
                .build();

        return jwtService.generateToken(system, Map.of("internal", true, "auth", List.of("SYSTEM")));
    }
}
