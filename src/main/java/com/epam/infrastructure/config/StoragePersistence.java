package com.epam.infrastructure.config;

import com.epam.infrastructure.annotations.MapStorage;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

@Component
public class StoragePersistence {

    private static final Logger logger = LoggerFactory.getLogger(StoragePersistence.class);
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${data.folder}")
    private String dataFolder;

    @PreDestroy
    public void persistAll() {
        logger.info("Persisting all @MapStorage beans before shutdown...");

        String basePath = dataFolder + "/";

        Map<String, Object> beans = ApplicationContextProvider.getContext().getBeansWithAnnotation(MapStorage.class);
        beans.forEach((name, bean) -> {
            MapStorage annotation = bean.getClass().getAnnotation(MapStorage.class);
            String path = basePath + annotation.file();

            try {
                Field storageField = bean.getClass().getDeclaredField("storage");
                if (Map.class.isAssignableFrom(storageField.getType())) {
                    storageField.setAccessible(true);
                    Object data = storageField.get(bean);
                    mapper.writerWithDefaultPrettyPrinter().writeValue(new File(path), data);
                    logger.info("Persisted {}", path);
                }
            } catch (Exception e) {
                logger.error("Failed to persist {}", path, e);
            }
        });
    }
}
