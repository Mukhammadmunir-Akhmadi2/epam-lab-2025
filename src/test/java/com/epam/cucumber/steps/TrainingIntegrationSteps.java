package com.epam.cucumber.steps;

import com.epam.cucumber.contracts.TrainerTrainingSummaryResponseDto;
import com.epam.cucumber.client.TrainingServiceClient;
import com.epam.cucumber.client.WorkloadServiceClient;
import com.epam.infrastructure.dtos.AuthDto;
import com.epam.infrastructure.dtos.TokenDto;
import com.epam.infrastructure.dtos.TraineeRegistrationRequest;
import com.epam.infrastructure.dtos.TrainerRegistrationRequest;
import com.epam.infrastructure.dtos.TrainingDto;
import com.epam.infrastructure.enums.TrainingTypeEnum;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.awaitility.Awaitility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class TrainingIntegrationSteps {

    @Autowired
    private TrainingServiceClient trainingServiceClient;

    @Autowired
    private WorkloadServiceClient workloadServiceClient;

    private int responseStatus;

    private String traineeUsername;
    private String traineePassword;
    private String traineeToken;

    private String trainerUsername;
    private String anotherTraineeUsername;

    @Value("${system.admin.username}")
    private String adminUsername;

    @Value("${system.admin.password}")
    private String adminPassword;

    @Given("a trainer exists")
    public void createTrainer() {
        TrainerRegistrationRequest req = new TrainerRegistrationRequest();
        req.setFirstName("John");
        req.setLastName("Trainer");
        req.setSpecialization(TrainingTypeEnum.CARDIO);

        ResponseEntity<AuthDto> response = trainingServiceClient.createTrainer(req);

        assertEquals(201, response.getStatusCode().value());
        trainerUsername = Objects.requireNonNull(response.getBody()).getUsername();
    }

    @Given("a trainee exists")
    public void createTrainee() {
        TraineeRegistrationRequest req = new TraineeRegistrationRequest();
        req.setFirstName("Mike");
        req.setLastName("Student");
        req.setDateOfBirth("2010-05-05");

        ResponseEntity<AuthDto> response = trainingServiceClient.createTrainee(req);

        assertEquals(201, response.getStatusCode().value());
        traineeUsername = Objects.requireNonNull(response.getBody()).getUsername();
        traineePassword = Objects.requireNonNull(response.getBody()).getPassword();
    }

    @Given("another trainee exists")
    public void createAnotherTrainee() {
        TraineeRegistrationRequest req = new TraineeRegistrationRequest();
        req.setFirstName("Another");
        req.setLastName("Trainee");
        req.setDateOfBirth("2005-01-01");

        ResponseEntity<AuthDto> response = trainingServiceClient.createTrainee(req);
        assertEquals(201, response.getStatusCode().value());
        anotherTraineeUsername = Objects.requireNonNull(response.getBody()).getUsername();
    }

    @Given("trainee is authenticated")
    public void authenticateTrainee() {
        AuthDto loginRequest = new AuthDto();
        loginRequest.setUsername(traineeUsername);
        loginRequest.setPassword(traineePassword);

        ResponseEntity<TokenDto> response = trainingServiceClient.login(loginRequest);
        assertEquals(200, response.getStatusCode().value());

        traineeToken = "Bearer " + Objects.requireNonNull(response.getBody()).getAccessToken();
    }

    @When("trainee creates training with trainer")
    public void createTraining() {
        TrainingDto dto = buildValidTrainingDto();

        ResponseEntity<Void> response =
                trainingServiceClient.createTraining(traineeUsername, trainerUsername, dto, traineeToken);

        responseStatus = response.getStatusCode().value();
    }

    @When("trainee creates another training at the same time with the same trainer")
    public void createOverlappingTraining() {
        TrainingDto dto = buildValidTrainingDto();
        try {
            ResponseEntity<Void> response =
                    trainingServiceClient.createTraining(traineeUsername, trainerUsername, dto, traineeToken);
            responseStatus = response.getStatusCode().value();
        } catch (feign.FeignException e) {
            responseStatus = e.status();
        }
    }

    @When("trainee creates training with non existing trainer")
    public void createTrainingWithNonExistingTrainer() {
        TrainingDto dto = buildValidTrainingDto();

        try {
            ResponseEntity<Void> response =
                    trainingServiceClient.createTraining(traineeUsername, "invalidTrainer", dto, traineeToken);
            responseStatus = response.getStatusCode().value();
        } catch (feign.FeignException e) {
            responseStatus = e.status();
        }
    }

    @When("trainee creates training with date in the past")
    public void createTrainingPast() {
        TrainingDto dto = new TrainingDto();
        dto.setName("Invalid Training");
        dto.setDuration(60);
        dto.setDate(LocalDateTime.now().minusDays(1).toString());
        dto.setType(TrainingTypeEnum.CARDIO);

        try {
            ResponseEntity<Void> response =
                    trainingServiceClient.createTraining(traineeUsername, trainerUsername, dto, traineeToken);
            responseStatus = response.getStatusCode().value();
        } catch (feign.FeignException e) {
            responseStatus = e.status();
        }
    }

    @When("trainee creates training without authentication")
    public void createTrainingWithoutAuthentication() {
        TrainingDto dto = buildValidTrainingDto();

        try {
            ResponseEntity<Void> response =
                    trainingServiceClient.createTraining(traineeUsername, trainerUsername, dto, "");
            responseStatus = response.getStatusCode().value();
        } catch (feign.FeignException e) {
            responseStatus = e.status();
        }
    }

    @When("trainee creates training with zero duration")
    public void createTrainingWithZeroDuration() {
        TrainingDto dto = new TrainingDto();
        dto.setName("Zero Duration Training");
        dto.setDuration(0);
        dto.setDate(LocalDateTime.now().plusDays(2).toString());
        dto.setType(TrainingTypeEnum.CARDIO);
        try {
            ResponseEntity<Void> response =
                    trainingServiceClient.createTraining(traineeUsername, trainerUsername, dto, traineeToken);
            responseStatus = response.getStatusCode().value();
        } catch (feign.FeignException e) {
            responseStatus = e.status();
        }
    }

    @When("trainee creates {int} trainings with {int} minutes each")
    public void createMultipleTrainings(int count, int duration) {
        for (int i = 0; i < count; i++) {
            TrainingDto dto = new TrainingDto();
            dto.setName("Training " + i);
            dto.setDuration(duration);
            dto.setDate(LocalDateTime.now().plusDays(2 + i).toString());
            dto.setType(TrainingTypeEnum.CARDIO);

            ResponseEntity<Void> response =
                    trainingServiceClient.createTraining(traineeUsername, trainerUsername, dto, traineeToken);
            assertEquals(201, response.getStatusCode().value());
        }
    }

    @When("trainee creates training using another trainee username")
    public void createTrainingForAnotherTrainee() {
        TrainingDto dto = buildValidTrainingDto();
        try {
            ResponseEntity<Void> response =
                    trainingServiceClient.createTraining(anotherTraineeUsername, trainerUsername, dto, traineeToken);
            responseStatus = response.getStatusCode().value();
        } catch (feign.FeignException e) {
            responseStatus = e.status();
        }
    }

    @When("trainee deletes account")
    public void deleteTrainee() {
        trainingServiceClient.deleteTrainee(traineeUsername, traineeToken);
    }

    @Then("trainer workload should increase by {int} minutes")
    public void verifyWorkload(int duration) {
        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> {
                    ResponseEntity<TrainerTrainingSummaryResponseDto> result = getTrainerWorkload();

                    TrainerTrainingSummaryResponseDto body = result.getBody();
                    assertNotNull(body);

                    long totalWorkload = calculateTotalWorkload(body);
                    assertEquals(duration, totalWorkload);
                });
    }

    @Then("trainer workload should be {int}")
    public void verifyZeroWorkload(int expected) {
        Awaitility.await()
                .atMost(Duration.ofSeconds(15))
                .pollInterval(Duration.ofMillis(500))
                .untilAsserted(() -> {
                    ResponseEntity<TrainerTrainingSummaryResponseDto> result = getTrainerWorkload();

                    TrainerTrainingSummaryResponseDto body = result.getBody();
                    assertNotNull(body);

                    long totalWorkload = calculateTotalWorkload(body);
                    assertEquals(expected, totalWorkload);
                });
    }

    @Then("request should fail with status {int}")
    public void verifyFailure(int status) {
        assertEquals(status, responseStatus);
    }

    private ResponseEntity<TrainerTrainingSummaryResponseDto> getTrainerWorkload() {
        String adminToken = authenticateAdmin();
        return workloadServiceClient.getTrainerWorkload(trainerUsername, adminToken);
    }

    private TrainingDto buildValidTrainingDto() {
        TrainingDto dto = new TrainingDto();
        dto.setName("Integration Training");
        dto.setDuration(60);
        dto.setDate(LocalDateTime.now().plusDays(2).toString());
        dto.setType(TrainingTypeEnum.CARDIO);
        return dto;
    }

    private long calculateTotalWorkload(TrainerTrainingSummaryResponseDto body) {
        return body.getYears().stream()
                .flatMap(year -> year.getMonths().stream())
                .mapToLong(TrainerTrainingSummaryResponseDto.TrainingMonthSummaryDto::getTrainingsSummaryDuration)
                .sum();
    }

    public String authenticateAdmin() {
        AuthDto loginRequest = new AuthDto();
        loginRequest.setUsername(adminUsername);
        loginRequest.setPassword(adminPassword);

        ResponseEntity<TokenDto> response = trainingServiceClient.login(loginRequest);

        assertEquals(200, response.getStatusCode().value());
        return "Bearer " + Objects.requireNonNull(response.getBody()).getAccessToken();
    }
}