package com.epam.infrastructure.config;

import com.epam.application.repository.BaseUserRepository;
import com.epam.infrastructure.security.CustomAccessDeniedHandler;
import com.epam.infrastructure.security.JwtAuthenticationEntryPoint;
import com.epam.infrastructure.security.filters.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(BaseUserRepository userRepository) {
        return username -> userRepository
                .findByUserName(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Invalid username or password")
                );
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter jwtFilter, JwtAuthenticationEntryPoint authEntrypoint, CustomAccessDeniedHandler accessDeniedHandler) {
        http
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors ->
                cors.configurationSource( request -> corsConfiguration()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/trainers").permitAll()
                        .requestMatchers(HttpMethod.POST, "/trainees").permitAll()
                        .requestMatchers("/users/login").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/trainees/**", "/trainings/trainee/**").hasAuthority("TRAINEE")
                        .requestMatchers("/trainers/**", "/trainings/trainer/**").hasAuthority("TRAINER")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authEntrypoint)
                        .accessDeniedHandler(accessDeniedHandler)
                );

        return http.build();
    }

    private CorsConfiguration corsConfiguration() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOriginPattern("*");
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedHeaders(List.of(
                HttpHeaders.CONTENT_TYPE,
                HttpHeaders.AUTHORIZATION,
                HttpHeaders.ACCEPT,
                "X-Transaction-Id"
        ));
        corsConfiguration.setAllowedMethods(List.of(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.PATCH.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.HEAD.name()
        ));
        corsConfiguration.setExposedHeaders(List.of(
                "X-Transaction-Id"
        ));
        corsConfiguration.setMaxAge(3600L);
        return corsConfiguration;
    }
}
