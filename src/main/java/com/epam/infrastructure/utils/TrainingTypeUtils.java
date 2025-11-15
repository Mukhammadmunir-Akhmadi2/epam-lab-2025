package com.epam.infrastructure.utils;

public class TrainingTypeUtils {
    public static String normalize(String type) {
        return type.trim().toUpperCase().replaceAll("\\s+", "_");
    }
}
