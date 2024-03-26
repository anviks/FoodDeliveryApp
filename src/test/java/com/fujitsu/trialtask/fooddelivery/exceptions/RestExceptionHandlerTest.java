package com.fujitsu.trialtask.fooddelivery.exceptions;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fujitsu.trialtask.fooddelivery.enums.Vehicle;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.context.request.WebRequest;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class RestExceptionHandlerTest {
    @Mock
    private WebRequest webRequest;

    @InjectMocks
    private RestExceptionHandler restExceptionHandler;

    @Test
    void handleHttpMessageNotReadable_InvalidEnumValue_ShouldReturnUnprocessableEntity() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();

        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);
        InvalidFormatException cause = mock(InvalidFormatException.class);
        when(exception.getCause()).thenReturn(cause);
        JsonMappingException.Reference reference = new JsonMappingException.Reference(null);
        when(cause.getPath()).thenReturn(List.of(reference));
        when(cause.getTargetType()).thenReturn((Class) Vehicle.class);

        // Act
        ResponseEntity<Object> responseEntity = restExceptionHandler.handleHttpMessageNotReadable(exception, headers, HttpStatus.BAD_REQUEST, webRequest);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, responseEntity.getStatusCode());
        ApiError apiError = (ApiError) responseEntity.getBody();
        assertNotNull(apiError);
    }

    @Test
    void handleHttpMessageNotReadable_MalformedJsonRequest_ShouldReturnBadRequest() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        HttpStatus status = HttpStatus.BAD_REQUEST;
        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);

        // Act
        ResponseEntity<Object> responseEntity = restExceptionHandler.handleHttpMessageNotReadable(exception, headers, status, webRequest);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(status, responseEntity.getStatusCode());
        ApiError apiError = (ApiError) responseEntity.getBody();
        assertNotNull(apiError);
        assertTrue(apiError.getErrorDetails().isEmpty());  // Ensure no error details are provided for a malformed JSON request
    }

    @Test
    void handleConstraintViolation_ShouldReturnResponseEntityWithCorrectDetails() {
        // Arrange
        ConstraintViolationException ex = mock(ConstraintViolationException.class);
        Set<ConstraintViolation<?>> constraintViolations = new HashSet<>();
        ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
        when(violation1.getMessage()).thenReturn("Violation 1 message");
        ConstraintViolation<?> violation2 = mock(ConstraintViolation.class);
        when(violation2.getMessage()).thenReturn("Violation 2 message");
        constraintViolations.add(violation1);
        constraintViolations.add(violation2);
        when(ex.getConstraintViolations()).thenReturn(constraintViolations);

        // Act
        ResponseEntity<Object> responseEntity = restExceptionHandler.handleConstraintViolation(ex);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, responseEntity.getStatusCode());
        ApiError apiError = (ApiError) responseEntity.getBody();
        assertNotNull(apiError);
        assertEquals(2, apiError.getErrorDetails().size());  // Ensure correct number of details in the response body
        assertTrue(apiError.getErrorDetails().contains("Violation 1 message"));
        assertTrue(apiError.getErrorDetails().contains("Violation 2 message"));
    }

    @Test
    void handleNotFound_ShouldReturnNotFoundResponse() {
        // Arrange
        String errorMessage = "RandomStringXYZ";
        EntityNotFoundException ex = new EntityNotFoundException(errorMessage);

        // Act
        ResponseEntity<Object> responseEntity = restExceptionHandler.handleNotFound(ex);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        ApiError apiError = (ApiError) responseEntity.getBody();
        assertNotNull(apiError);
        assertEquals(errorMessage, apiError.getErrorDetails().get(0));
    }

    @Test
    void handleVehicleForbidden_ShouldReturnUnprocessableEntity() {
        // Arrange
        String errorMessage = "RandomStringXYZ";
        ForbiddenVehicleException ex = new ForbiddenVehicleException(errorMessage);

        // Act
        ResponseEntity<Object> responseEntity = restExceptionHandler.handleVehicleForbidden(ex);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, responseEntity.getStatusCode());
        ApiError apiError = (ApiError) responseEntity.getBody();
        assertNotNull(apiError);
        assertEquals(errorMessage, apiError.getErrorDetails().get(0));
    }

    @Test
    void handleVehicleUnavailable_ShouldReturnUnprocessableEntity() {
        // Arrange
        String errorMessage = "RandomStringXYZ";
        UnavailableVehicleException ex = new UnavailableVehicleException(errorMessage);

        // Act
        ResponseEntity<Object> responseEntity = restExceptionHandler.handleVehicleUnavailable(ex);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, responseEntity.getStatusCode());
        ApiError apiError = (ApiError) responseEntity.getBody();
        assertNotNull(apiError);
        assertEquals(errorMessage, apiError.getErrorDetails().get(0));
    }

    @Test
    void handleDuplicateEntity_ShouldReturnConflict() {
        // Arrange
        String errorMessage = "RandomStringXYZ";
        EntityExistsException ex = new EntityExistsException(errorMessage);

        // Act
        ResponseEntity<Object> responseEntity = restExceptionHandler.handleDuplicateEntity(ex);

        // Assert
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        ApiError apiError = (ApiError) responseEntity.getBody();
        assertNotNull(apiError);
        assertEquals(errorMessage, apiError.getErrorDetails().get(0));
    }
}