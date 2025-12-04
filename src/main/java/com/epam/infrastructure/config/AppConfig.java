package com.epam.infrastructure.config;

import com.epam.application.repository.TrainingTypeRepository;
import com.epam.infrastructure.enums.TrainingTypeEnum;
import com.epam.infrastructure.logging.TransactionIdFilter;
import com.epam.model.TrainingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class AppConfig {
    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    @Bean
    public FilterRegistrationBean<TransactionIdFilter> txIdFilter(TransactionIdFilter filter) {
        FilterRegistrationBean<TransactionIdFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setOrder(1);
        reg.addUrlPatterns("/*");
        return reg;
    }

    @Bean
    @Profile("prod")
    public CommandLineRunner trainingTypeInitializer(TrainingTypeRepository trainingTypeRepository) {
        return args -> {
            for (TrainingTypeEnum typeEnum : TrainingTypeEnum.values()) {
                trainingTypeRepository.findByType(typeEnum).ifPresentOrElse(
                        existing -> logger.info("Training type {} already exists. Skipping creation.", typeEnum),
                        () -> {
                            TrainingType trainingType = new TrainingType();
                            trainingType.setTrainingType(typeEnum);
                            trainingTypeRepository.save(trainingType);
                            logger.info("Saved training type: {}", typeEnum);
                        }
                );
            }
        };
    }
}
