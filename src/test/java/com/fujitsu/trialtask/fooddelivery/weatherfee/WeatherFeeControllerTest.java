package com.fujitsu.trialtask.fooddelivery.weatherfee;

import com.fujitsu.trialtask.fooddelivery.enums.City;
import com.fujitsu.trialtask.fooddelivery.enums.Vehicle;
import com.fujitsu.trialtask.fooddelivery.enums.WeatherCondition;
import com.fujitsu.trialtask.fooddelivery.weatherfee.WeatherFee;
import com.fujitsu.trialtask.fooddelivery.weatherfee.WeatherFeeController;
import com.fujitsu.trialtask.fooddelivery.weatherfee.WeatherFeeRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherFeeControllerTest {

    @Mock
    private WeatherFeeRepository weatherFeeRepository;

    @InjectMocks
    private WeatherFeeController controller;

    @Test
    void getAllWeatherFees_ReturnsWeatherFees() {
        // Arrange
        List<WeatherFee> expectedFees = List.of(
                new WeatherFee(Vehicle.CAR, WeatherCondition.WIND_SPEED, 10.0f, 20.0f, 1.2f),
                new WeatherFee(Vehicle.BIKE, WeatherCondition.AIR_TEMPERATURE, 15.0f, 25.0f, 1.5f));
        when(weatherFeeRepository.findAll()).thenReturn(expectedFees);

        // Act
        ResponseEntity<List<WeatherFee>> response = controller.getAllWeatherFees();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedFees, response.getBody());
    }

    @Test
    void getWeatherFeeById_WithExistingId_ReturnsWeatherFee() {
        // Arrange
        long id = 1L;
        WeatherFee weatherFee = new WeatherFee(Vehicle.CAR, WeatherCondition.WIND_SPEED, 10.0f, 20.0f, 1.2f);
        when(weatherFeeRepository.findById(id)).thenReturn(Optional.of(weatherFee));

        // Act
        ResponseEntity<WeatherFee> response = controller.getWeatherFeeById(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(weatherFee, response.getBody());
    }

    @Test
    void getWeatherFeeById_WithNonExistingId_ThrowsEntityNotFoundException() {
        // Arrange
        long id = 1L;
        when(weatherFeeRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> controller.getWeatherFeeById(id));
    }

    @Test
    void createWeatherFee_WithValidWeatherFee_ReturnsCreated() {
        // Arrange
        WeatherFee weatherFee = new WeatherFee(Vehicle.CAR, WeatherCondition.WIND_SPEED, 10.0f, 20.0f, 1.2f);
        when(weatherFeeRepository.save(weatherFee)).thenReturn(weatherFee);
        mockNoConflicts();

        // Act
        ResponseEntity<WeatherFee> response = controller.createWeatherFee(weatherFee);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(weatherFee, response.getBody());
    }

    @Test
    void createWeatherFee_WithPartiallyOverlappingRange_ThrowsEntityExistsException() {
        // Arrange
        WeatherFee existingWeatherFee = new WeatherFee(Vehicle.CAR, WeatherCondition.WIND_SPEED, 10.0f, 20.0f, 1.2f);
        existingWeatherFee.setId(6L);
        mockNoLessStrictHigherPhenomenonFee();
        when(weatherFeeRepository.findOverlappingRange(any(), any(), any(), any())).thenReturn(existingWeatherFee.getId());
        WeatherFee newWeatherFee = new WeatherFee(Vehicle.CAR, WeatherCondition.WIND_SPEED, 15.0f, 25.0f, 1.5f);

        // Act & Assert
        assertThrows(EntityExistsException.class, () -> controller.createWeatherFee(newWeatherFee));
    }

    @Test
    void createWeatherFee_WithLessStrictHigherPhenomenonFee_ThrowsEntityExistsException() {
        // Arrange
        WeatherFee existingWeatherFee = new WeatherFee(Vehicle.CAR, WeatherCondition.WIND_SPEED, 10.0f, 20.0f, 1.2f);
        existingWeatherFee.setId(6L);
        mockNoOverlap();
        when(weatherFeeRepository.findLessStrictHigherPhenomenonFee(any(), any(), any(), any())).thenReturn(existingWeatherFee.getId());
        WeatherFee newWeatherFee = new WeatherFee(Vehicle.CAR, WeatherCondition.WIND_SPEED, 15.0f, 25.0f, 1.5f);

        // Act & Assert
        EntityExistsException exception = assertThrows(EntityExistsException.class, () -> controller.createWeatherFee(newWeatherFee));
        assertTrue(exception.getMessage().contains(existingWeatherFee.getId().toString()));
    }

    @Test
    void updateWeatherFee_WithExistingIdAndValidWeatherFee_ReturnsUpdated() {
        // Arrange
        long id = 1L;
        WeatherFee updatedWeatherFee = new WeatherFee(Vehicle.BIKE, WeatherCondition.AIR_TEMPERATURE, 15.0f, 25.0f, 1.5f);
        when(weatherFeeRepository.findById(id)).thenReturn(Optional.of(new WeatherFee()));
        when(weatherFeeRepository.save(updatedWeatherFee)).thenReturn(updatedWeatherFee);
        mockNoConflicts();

        // Act
        ResponseEntity<WeatherFee> response = controller.updateWeatherFee(id, updatedWeatherFee);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedWeatherFee, response.getBody());
    }

    @Test
    void updateWeatherFee_WithNonExistingId_ThrowsEntityNotFoundException() {
        // Arrange
        long id = 1L;
        when(weatherFeeRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> controller.updateWeatherFee(id, new WeatherFee()));
    }

    @Test
    void patchWeatherFee_WithExistingIdAndValidPartialWeatherFee_ReturnsPatched() {
        // Arrange
        long id = 1L;
        WeatherFee existingWeatherFee = new WeatherFee(Vehicle.CAR, WeatherCondition.WIND_SPEED, 10.0f, 20.0f, 1.2f);
        WeatherFee partialWeatherFee = new WeatherFee();
        partialWeatherFee.setVehicle(Vehicle.BIKE);
        partialWeatherFee.setFee(15.0f);

        when(weatherFeeRepository.findById(id)).thenReturn(Optional.of(existingWeatherFee));
        when(weatherFeeRepository.save(existingWeatherFee)).thenReturn(existingWeatherFee);
        mockNoConflicts();

        // Act
        ResponseEntity<WeatherFee> response = controller.patchWeatherFee(id, partialWeatherFee);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(partialWeatherFee.getVehicle(), existingWeatherFee.getVehicle());
        assertEquals(partialWeatherFee.getFee(), existingWeatherFee.getFee());
    }

    @Test
    void patchWeatherFee_WithNonExistingId_ThrowsEntityNotFoundException() {
        // Arrange
        long id = 1L;
        when(weatherFeeRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> controller.patchWeatherFee(id, new WeatherFee()));
    }

    @Test
    void deleteWeatherFee_WithExistingId_ReturnsNoContent() {
        // Arrange
        long id = 1L;
        doNothing().when(weatherFeeRepository).deleteById(id);

        // Act
        ResponseEntity<Void> response = controller.deleteWeatherFee(id);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(weatherFeeRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteWeatherFee_WithNonExistingId_ThrowsEntityNotFoundException() {
        // Arrange
        long id = 1L;
        doThrow(EntityNotFoundException.class).when(weatherFeeRepository).deleteById(id);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> controller.deleteWeatherFee(id));
    }

    private void mockNoConflicts() {
        mockNoLessStrictHigherPhenomenonFee();
        mockNoOverlap();
    }

    private void mockNoLessStrictHigherPhenomenonFee() {
        lenient().when(weatherFeeRepository.findLessStrictHigherPhenomenonFee(any(), any(), any(), any())).thenReturn(null);
    }

    private void mockNoOverlap() {
        lenient().when(weatherFeeRepository.findOverlappingRange(any(), any(), any(), any())).thenReturn(null);
    }
}
