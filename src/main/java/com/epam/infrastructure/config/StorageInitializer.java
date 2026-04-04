package com.epam.infrastructure.config;

import com.epam.infrastructure.annotations.MapStorage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Map;

@Log4j2
@Component
@Profile("dev")
@RequiredArgsConstructor
public class StorageInitializer implements BeanPostProcessor {
    @Value("${data.folder}")
    private String dataFolder;

    private final ObjectMapper mapper;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();

        if (clazz.isAnnotationPresent(MapStorage.class)) {
            MapStorage annotation = clazz.getAnnotation(MapStorage.class);
            String fileName  = annotation.file();
            String path = dataFolder + "/" + fileName ;

            File file = new File(path);

            if (!file.exists()) {
                log.warn("File {} not found. Skipping initialization for {}", path, beanName);
                return bean;
            }

            try (FileInputStream inputStream = new FileInputStream(file)) {
                Field storageField = clazz.getDeclaredField("storage");

                if (Map.class.isAssignableFrom(storageField.getType())) {
                    storageField.setAccessible(true);
                    Type genericType = storageField.getGenericType();

                    TypeReference<?> typeRef = new TypeReference<Object>() {
                        @Override
                        public java.lang.reflect.Type getType() {
                            return genericType;
                        }
                    };

                    Object data = mapper.readValue(inputStream, typeRef);

                    storageField.set(bean, data);
                    log.info("Loaded data from {} into {}", file, beanName);
                }
            } catch (NoSuchFieldException e) {
                log.warn("'storage' field not found in {}. Skipping initialization.", beanName);
            } catch (Exception e) {
                log.error("Failed to initialize {} from {}", beanName, path);
            }
        }
        return bean;
    }
}
