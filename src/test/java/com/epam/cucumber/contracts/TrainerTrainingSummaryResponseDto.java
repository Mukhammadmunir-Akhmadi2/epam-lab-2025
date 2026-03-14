package com.epam.cucumber.contracts;

import lombok.Data;
import java.util.List;

@Data
public class TrainerTrainingSummaryResponseDto {

    private String username;
    private String firstName;
    private String lastName;
    private Boolean status;
    private List<TrainingYearSummaryDto> years;

    @Data
    public static class TrainingYearSummaryDto {
        private int year;
        private List<TrainingMonthSummaryDto> months;
    }

    @Data
    public static class TrainingMonthSummaryDto {
        private int month;
        private long trainingsSummaryDuration;
    }
}