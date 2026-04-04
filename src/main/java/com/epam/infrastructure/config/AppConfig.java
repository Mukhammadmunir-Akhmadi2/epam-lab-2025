package com.epam.infrastructure.config;

import com.epam.application.repository.RoleRepository;
import com.epam.application.repository.TrainingTypeRepository;
import com.epam.infrastructure.enums.RoleEnum;
import com.epam.infrastructure.enums.TrainingTypeEnum;
import com.epam.infrastructure.logging.TransactionIdFilter;
import com.epam.infrastructure.repository.JpaBaseUserRepository;
import com.epam.infrastructure.repository.JpaRoleRepository;
import com.epam.model.Role;
import com.epam.model.TrainingType;
import com.epam.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Log4j2
@Configuration
public class AppConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    @Bean
    public FilterRegistrationBean<TransactionIdFilter> txIdFilter(TransactionIdFilter filter) {
        FilterRegistrationBean<TransactionIdFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setOrder(1);
        reg.addUrlPatterns("/*");
        return reg;
    }

    @Bean
    @Profile({"local", "stg", "dev", "prod"})
    public CommandLineRunner trainingTypeInitializer(TrainingTypeRepository trainingTypeRepository) {
        return args -> {
            for (TrainingTypeEnum typeEnum : TrainingTypeEnum.values()) {
                trainingTypeRepository.findByType(typeEnum).ifPresentOrElse(
                        existing -> log.info("Training type {} already exists. Skipping creation.", typeEnum),
                        () -> {
                            TrainingType trainingType = new TrainingType();
                            trainingType.setTrainingType(typeEnum);
                            trainingTypeRepository.save(trainingType);
                            log.info("Saved training type: {}", typeEnum);
                        }
                );
            }
        };
    }

    @Bean
    @Profile("dev")
    public CommandLineRunner roleInitializer(RoleRepository roleRepository) {
        return args -> {
            for (RoleEnum roleEnum : RoleEnum.values()) {
                roleRepository.findByName(roleEnum).ifPresentOrElse(
                        existing -> log.info("Role {} already exists. Skipping creation.", roleEnum),
                        () -> {
                            Role role = new Role();
                            role.setRole(roleEnum);
                            roleRepository.save(role);
                            log.info("Saved role: {}", roleEnum);
                        }
                );
            }
        };
    }

    @Bean
    @Profile({"local", "stg", "prod"})
    public CommandLineRunner adminInitializer(
            JpaBaseUserRepository userRepository,
            JpaRoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            @Value("${system.admin.username}") String adminUsername,
            @Value("${system.admin.password}") String adminPassword
    ) {
        return args -> {
            userRepository.findByUsername(adminUsername).ifPresentOrElse(
                    existing -> log.info("Admin user '{}' already exists. Skipping creation.", adminUsername),
                    () -> {
                        var roles = roleRepository.findAll();
                        User admin = new User();
                        admin.setFirstName("User");
                        admin.setLastName("User");
                        admin.setUsername(adminUsername);
                        admin.getRoles().addAll(roles);
                        admin.setIsActive(true);

                        admin.setPassword(passwordEncoder.encode(adminPassword));

                        userRepository.save(admin);
                        log.info("Created admin user '{}' (local profile).", adminUsername);
                    }
            );
        };
    }
}
