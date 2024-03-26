package com.fujitsu.trialtask.fooddelivery.delivery;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryControllerTest {
    @Mock
    private DeliveryFeeCalculator deliveryFeeCalculator;

    @InjectMocks
    private DeliveryController deliveryController;


    @ParameterizedTest
    @CsvSource({
            "TALLINN, CAR",
            "TARTU, BIKE",
            "PÄRNU, SCOOTER",
            "tallinn, bike",
            "TARTU, scooter",
            "pärnu, CAR"
    })
    void getDeliveryFee_WithValidCityAndVehicleLowerAndUpper_ShouldReturnResponseEntityWithFee(String cityName, String vehicleName) {
        // Arrange
        float expectedFee = 21.9f;
        when(deliveryFeeCalculator.calculate(any(), any())).thenReturn(expectedFee);

        // Act
        ResponseEntity<Map<String, Object>> response = deliveryController.getDeliveryFee(cityName, vehicleName);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(cityName.toUpperCase(), responseBody.get("city"));
        assertEquals(vehicleName.toUpperCase(), responseBody.get("vehicle"));
        assertEquals(expectedFee, responseBody.get("fee"));
    }

    @Test
    void getDeliveryFee_WithInvalidCity_ShouldThrowNotFoundException() {
        // Arrange
        String cityName = "INVALID_CITY";
        String vehicleName = "CAR";

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> deliveryController.getDeliveryFee(cityName, vehicleName));
        verify(deliveryFeeCalculator, never()).calculate(any(), any());
    }

    @Test
    void getDeliveryFee_WithInvalidVehicle_ShouldThrowNotFoundException() {
        // Arrange
        String cityName = "TALLINN";
        String vehicleName = "INVALID_VEHICLE";

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> deliveryController.getDeliveryFee(cityName, vehicleName));
        verify(deliveryFeeCalculator, never()).calculate(any(), any());
    }
}