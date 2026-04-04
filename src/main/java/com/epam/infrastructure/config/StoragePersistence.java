package com.epam.infrastructure.config;

import com.epam.infrastructure.annotations.MapStorage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Log4j2
@Profile("dev")
public class StoragePersistence {

    private final ObjectMapper mapper;
    private final ApplicationContext context;

    @Value("${data.folder}")
    private String dataFolder;

    @PreDestroy
    public void persistAll() {
        log.info("Persisting all @MapStorage beans before shutdown...");

        String basePath = dataFolder + "/";

        Map<String, Object> beans = context.getBeansWithAnnotation(MapStorage.class);
        beans.forEach((name, bean) -> {
            MapStorage annotation = bean.getClass().getAnnotation(MapStorage.class);
            String path = basePath + annotation.file();

            try {
                Field storageField = bean.getClass().getDeclaredField("storage");
                if (Map.class.isAssignableFrom(storageField.getType())) {
                    storageField.setAccessible(true);
                    Object data = storageField.get(bean);
                    mapper.writerWithDefaultPrettyPrinter().writeValue(new File(path), data);
                    log.info("Persisted {}", path);
                }
            } catch (Exception e) {
                log.error("Failed to persist {}", path, e);
            }
        });
    }
}
