package com.epam.infrastructure.mappers;

import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;

public class CommonMapper {
    private static final DateTimeFormatter[] LOCAL_DATE_TIME_FORMATTERS = new DateTimeFormatter[]{
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm"),
            DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")
    };

    private static final DateTimeFormatter[] LOCAL_DATE_FORMATTERS = new DateTimeFormatter[]{
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("yyyy:MM:dd")
    };

    @Named("uuidToString")
    public static String uuidToString(UUID id) {
        return id != null ? id.toString() : null;
    }

    @Named("stringToUuid")
    public static UUID stringToUuid(String id) {
        return id != null ? UUID.fromString(id) : null;
    }

    @Named("parseLocalDateTime")
    public static LocalDateTime parseLocalDateTime(String dateTime) {
        if (dateTime == null || dateTime.isBlank()) return null;

        for (DateTimeFormatter formatter : LOCAL_DATE_TIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(dateTime, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }
        throw new IllegalArgumentException("Cannot parse LocalDateTime: " + dateTime);
    }

    @Named("formatLocalDateTime")
    public static String formatLocalDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Named("parseLocalDate")
    public static LocalDate parseLocalDate(String date) {
        if (date == null || date.isBlank()) return null;

        for (DateTimeFormatter formatter : LOCAL_DATE_FORMATTERS) {
            try {
                return LocalDate.parse(date, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }
        throw new IllegalArgumentException("Cannot parse LocalDate: " + date);
    }

    @Named("formatLocalDate")
    public static String formatLocalDate(LocalDate date) {
        if (date == null) return null;
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
