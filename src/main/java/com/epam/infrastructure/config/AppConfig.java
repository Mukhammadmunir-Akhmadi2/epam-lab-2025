package com.epam.infrastructure.config;

import com.epam.application.repository.TrainingTypeRepository;
import com.epam.infrastructure.enums.TrainingTypeEnum;
import com.epam.infrastructure.logging.TransactionIdFilter;
import com.epam.infrastructure.repository.JpaBaseUserRepository;
import com.epam.infrastructure.repository.JpaRoleRepository;
import com.epam.model.TrainingType;
import com.epam.model.User;
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
    public FilterRegistrationBean<TransactionIdFilter> txIdFilter(TransactionIdFilter filter) {
        FilterRegistrationBean<TransactionIdFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setOrder(1);
        reg.addUrlPatterns("/*");
        return reg;
    }

    @Bean
    @Profile("local")
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
    @Profile("local")
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
