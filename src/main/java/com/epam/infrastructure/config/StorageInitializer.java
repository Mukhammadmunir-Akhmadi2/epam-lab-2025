package com.epam.infrastructure.config;

import com.epam.infrastructure.annotations.MapStorage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Map;

@Component
public class StorageInitializer implements BeanPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(StorageInitializer.class);

    @Value("${data.folder}")
    private String dataFolder;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();

        if (clazz.isAnnotationPresent(MapStorage.class)) {
            MapStorage annotation = clazz.getAnnotation(MapStorage.class);
            String file = annotation.file();
            String path = dataFolder + "/" + file;

            try (FileInputStream inputStream = new FileInputStream(path)) {
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
                    logger.info("Loaded data from {} into {}", file, beanName);}

            } catch (NoSuchFieldException e) {
                logger.warn("'storage' field not found in {}. Skipping initialization.", beanName);
            } catch (Exception e) {
                logger.error("Failed to initialize {} from {}", beanName, path);
            }
        }
        return bean;
    }
}
