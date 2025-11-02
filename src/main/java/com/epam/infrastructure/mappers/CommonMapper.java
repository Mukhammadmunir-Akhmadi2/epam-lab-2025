package com.epam.infrastructure.mappers;

import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class CommonMapper {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Named("uuidToString")
    public static String uuidToString(UUID id) {
        return id != null ? id.toString() : null;
    }

    @Named("stringToUuid")
    public static UUID stringToUuid(String id) {
        return id != null ? UUID.fromString(id) : null;
    }

    @Named("localDateTimeToString")
    public String localDateTimeToString(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(FORMATTER) : null;
    }

    @Named("stringToLocalDateTime")
    public LocalDateTime stringToLocalDateTime(String dateTime) {
        return dateTime != null ? LocalDateTime.parse(dateTime, FORMATTER) : null;
    }

    @Named("localDateToString")
    public String localDateToString(LocalDate date) {
        return date != null ? date.toString() : null;
    }

    @Named("stringToLocalDate")
    public LocalDate stringToLocalDate(String date) {
        return date != null ? LocalDate.parse(date) : null;
    }

}
