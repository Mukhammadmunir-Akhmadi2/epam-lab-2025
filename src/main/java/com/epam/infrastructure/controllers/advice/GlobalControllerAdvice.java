package com.epam.infrastructure.controllers.advice;

import com.epam.application.exceptions.InvalidCredentialException;
import com.epam.application.exceptions.ResourceNotFoundException;
import com.epam.application.exceptions.UnauthorizedAccess;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.Instant;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalControllerAdvice {
    private static final Logger log = LoggerFactory.getLogger(GlobalControllerAdvice.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFound(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage(), ex);
        ProblemDetail problem = createProblemDetail(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(InvalidCredentialException.class)
    public ResponseEntity<ProblemDetail> handleInvalidCredentials(InvalidCredentialException ex) {
        log.warn("Invalid credentials: {}", ex.getMessage(), ex);
        ProblemDetail problem = createProblemDetail(HttpStatus.BAD_REQUEST, "Invalid Credentials", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(UnauthorizedAccess.class)
    public ResponseEntity<ProblemDetail> handleUnauthorized(UnauthorizedAccess ex) {
        log.warn("Unauthorized access: {}", ex.getMessage(), ex);
        ProblemDetail problem = createProblemDetail(HttpStatus.UNAUTHORIZED, "Unauthorized Access", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage(), ex);
        ProblemDetail problem = createProblemDetail(HttpStatus.BAD_REQUEST, "Illegal Argument", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(ConstraintViolationException ex) {
        String errors = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining("; "));
        log.warn("Constraint violation: {}", errors);
        ProblemDetail problem = createProblemDetail(HttpStatus.BAD_REQUEST, "Constraint Violation", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("Validation failed: {}", errors);
        ProblemDetail problem = createProblemDetail(HttpStatus.BAD_REQUEST, "Validation Error", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ProblemDetail> handleHandlerMethodValidation(HandlerMethodValidationException ex) {

        String errors = ex.getParameterValidationResults().stream()
                .map(result -> result.getMethodParameter().getParameterName() + ": " +
                        result.getResolvableErrors().stream()
                                .map(Object::toString)
                                .collect(Collectors.joining(", ")))
                .collect(Collectors.joining("; "));

        String crossErrors = ex.getCrossParameterValidationResults().stream()
                .map(Object::toString)
                .collect(Collectors.joining("; "));

        String combinedErrors = errors;
        if (!crossErrors.isEmpty()) {
            combinedErrors += (combinedErrors.isEmpty() ? "" : "; ") + crossErrors;
        }

        log.warn("Handler method validation failed: {}", combinedErrors);

        ProblemDetail problem = createProblemDetail(HttpStatus.BAD_REQUEST, "Validation Error", combinedErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneric(Exception ex) {
        log.error("Internal server error: {}", ex.getMessage(), ex);
        ProblemDetail problem = createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }

    private ProblemDetail createProblemDetail(HttpStatus status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setProperty("timestamp", Instant.now().toString());
        return problem;
    }
}
