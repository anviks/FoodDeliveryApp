package com.fujitsu.trialtask.fooddelivery.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Global exception handler for REST controllers.
 * Handles various exceptions and returns appropriate error responses.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    // Constants for error messages
    private static final String CONSTRAINT_VIOLATION_MESSAGE = "Constraint violation";
    private static final String MALFORMED_JSON_MESSAGE = "Malformed JSON request";
    private static final String NOT_FOUND_MESSAGE = "Resource not found";
    private static final String INVALID_TYPE_MESSAGE = "Invalid type";
    private static final String INVALID_VALUE_MESSAGE = "Invalid value";
    private static final String VEHICLE_FORBIDDEN_MESSAGE = "Usage of selected vehicle type is forbidden";
    private static final String VEHICLE_UNAVAILABLE_MESSAGE = "Vehicle is unavailable in the specified city";
    private static final String DUPLICATE_ENTITY_MESSAGE = "Entity already exists";

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  @NotNull HttpHeaders headers,
                                                                  @NotNull HttpStatusCode status,
                                                                  @NotNull WebRequest request) {
        var errorDetails = new ArrayList<String>();
        HttpStatus httpStatus;
        String message;

        if (ex.getCause() instanceof InvalidFormatException cause) {
            String fieldName = cause.getPath().get(0).getFieldName();
            Object value = cause.getValue();
            Class<?> targetType = cause.getTargetType();

            String detail = "Invalid value for field '" + fieldName + "' (" + value + ")";
            message = INVALID_VALUE_MESSAGE;

            if (targetType.isEnum()) {
                detail += ", allowed values are " + Arrays.toString(targetType.getEnumConstants());
            } else if (!targetType.isAssignableFrom(value.getClass())) {
                message = INVALID_TYPE_MESSAGE;
                detail = "Invalid type for field '" + fieldName + "', expected " + targetType.getSimpleName() + " but got " + value.getClass().getSimpleName();
            }

            errorDetails.add(detail);
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
        } else {
            message = MALFORMED_JSON_MESSAGE;
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        return buildResponseEntity(new ApiError(httpStatus, message, errorDetails));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.UNPROCESSABLE_ENTITY,
                CONSTRAINT_VIOLATION_MESSAGE,
                ex.getConstraintViolations()
                        .stream()
                        .map(ConstraintViolation::getMessage)
                        .toList());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleNotFound(EntityNotFoundException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND,
                NOT_FOUND_MESSAGE,
                List.of(ex.getMessage()));
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(ForbiddenVehicleException.class)
    protected ResponseEntity<Object> handleVehicleForbidden(ForbiddenVehicleException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.UNPROCESSABLE_ENTITY,
                VEHICLE_FORBIDDEN_MESSAGE,
                List.of(ex.getMessage()));
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(UnavailableVehicleException.class)
    protected ResponseEntity<Object> handleVehicleUnavailable(UnavailableVehicleException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.UNPROCESSABLE_ENTITY,
                VEHICLE_UNAVAILABLE_MESSAGE,
                List.of(ex.getMessage()));
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(EntityExistsException.class)
    protected ResponseEntity<Object> handleDuplicateEntity(EntityExistsException ex) {
        ApiError apiError = new ApiError(
                HttpStatus.CONFLICT,
                DUPLICATE_ENTITY_MESSAGE,
                List.of(ex.getMessage()));
        return buildResponseEntity(apiError);
    }
}
