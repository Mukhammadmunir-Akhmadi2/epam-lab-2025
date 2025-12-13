package com.epam.infrastructure.controllers.advice;

import com.epam.application.exceptions.ResourceNotFoundException;
import com.epam.application.exceptions.UnauthorizedAccess;
import com.epam.infrastructure.utils.ProblemDetailUtil;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalControllerAdvice {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalControllerAdvice.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFound(ResourceNotFoundException ex) {

        LOGGER.error("Resource not found: {}", ex.getMessage());
        LOGGER.debug("Stack trace: ", ex);

        ProblemDetail problem = ProblemDetailUtil.createProblemDetail(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
    public ResponseEntity<ProblemDetail> handleInvalidCredentials(Exception ex) {
        LOGGER.warn("Invalid credentials: {}", ex.getMessage());
        LOGGER.debug("Stack trace: ", ex);

        ProblemDetail problem = ProblemDetailUtil.createProblemDetail(HttpStatus.BAD_REQUEST, "Invalid Credentials", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(UnauthorizedAccess.class)
    public ResponseEntity<ProblemDetail> handleUnauthorized(UnauthorizedAccess ex) {

        LOGGER.warn("Unauthorized access: {}", ex.getMessage());
        LOGGER.debug("Stack trace: ", ex);

        ProblemDetail problem = ProblemDetailUtil.createProblemDetail(HttpStatus.UNAUTHORIZED, "Unauthorized Access", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(IllegalArgumentException ex) {

        LOGGER.warn("Illegal argument: {}", ex.getMessage());
        LOGGER.debug("Stack trace: ", ex);

        ProblemDetail problem = ProblemDetailUtil.createProblemDetail(HttpStatus.BAD_REQUEST, "Illegal Argument", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(ConstraintViolationException ex) {
        String errors = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining("; "));

        LOGGER.warn("Constraint violation: {}", errors);
        LOGGER.debug("Stack trace: ", ex);

        ProblemDetail problem = ProblemDetailUtil.createProblemDetail(HttpStatus.BAD_REQUEST, "Constraint Violation", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));

        LOGGER.warn("Validation failed: {}", errors);
        LOGGER.debug("Stack trace: ", ex);

        ProblemDetail problem = ProblemDetailUtil.createProblemDetail(HttpStatus.BAD_REQUEST, "Validation Error", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        LOGGER.warn("missing request body: {}", ex.getMessage());
        LOGGER.debug("Stack trace: ", ex);

        ProblemDetail problem = ProblemDetailUtil.createProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Missing Request Body",
                "Request body is missing or invalid JSON"
        );
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

        LOGGER.warn("Handler method validation failed: {}", combinedErrors);
        LOGGER.debug("Stack trace: ", ex);

        ProblemDetail problem = ProblemDetailUtil.createProblemDetail(HttpStatus.BAD_REQUEST, "Validation Error", combinedErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneric(Exception ex) {
        LOGGER.error("Internal server error: {}", ex.getMessage());
        LOGGER.debug("Stack trace: ", ex);
        ProblemDetail problem = ProblemDetailUtil.createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }
}
