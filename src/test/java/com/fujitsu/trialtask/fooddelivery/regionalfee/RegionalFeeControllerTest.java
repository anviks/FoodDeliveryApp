package com.fujitsu.trialtask.fooddelivery.regionalfee;

import com.fujitsu.trialtask.fooddelivery.enums.City;
import com.fujitsu.trialtask.fooddelivery.enums.Vehicle;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegionalFeeControllerTest {

    @Mock
    private RegionalFeeRepository regionalFeeRepository;

    @InjectMocks
    private RegionalFeeController controller;

    @Test
    void getAllRegionalFees_ReturnsListOfRegionalFees() {
        // Arrange
        List<RegionalFee> expectedFees = new ArrayList<>(List.of(
                new RegionalFee(City.TALLINN, Vehicle.CAR, 1.0f),
                new RegionalFee(City.TARTU, Vehicle.BIKE, 2.0f),
                new RegionalFee(City.PÄRNU, Vehicle.SCOOTER, 3.0f)
        ));
        when(regionalFeeRepository.findAll()).thenReturn(expectedFees);

        // Act
        ResponseEntity<Iterable<RegionalFee>> response = controller.getAllRegionalFees();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedFees, response.getBody());
    }

    @Test
    void getRegionalFeeById_WithExistingId_ReturnsRegionalFee() {
        // Arrange
        RegionalFee regionalFee = new RegionalFee(City.TALLINN, Vehicle.CAR, 1.0f);
        long id = 976L;
        when(regionalFeeRepository.findById(id)).thenReturn(Optional.of(regionalFee));

        // Act
        ResponseEntity<RegionalFee> response = controller.getRegionalFeeById(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(regionalFee, response.getBody());
    }

    @Test
    void getRegionalFeeById_WithNonExistingId_ThrowsEntityNotFoundException() {
        // Arrange
        long id = 1L;
        when(regionalFeeRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> controller.getRegionalFeeById(id));
    }

    @Test
    void createRegionalFee_WithUniqueCityAndVehicle_ReturnsCreatedRegionalFee() {
        // Arrange
        RegionalFee regionalFee = new RegionalFee();
        when(regionalFeeRepository.existsByCityAndVehicle(any(), any())).thenReturn(false);
        when(regionalFeeRepository.save(regionalFee)).thenReturn(regionalFee);

        // Act
        ResponseEntity<RegionalFee> response = controller.createRegionalFee(regionalFee);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(regionalFee, response.getBody());
    }

    @Test
    void createRegionalFee_WithExistingCityAndVehicle_ThrowsEntityExistsException() {
        // Arrange
        RegionalFee regionalFee = new RegionalFee();
        when(regionalFeeRepository.existsByCityAndVehicle(any(), any())).thenReturn(true);

        // Act & Assert
        assertThrows(EntityExistsException.class, () -> controller.createRegionalFee(regionalFee));
    }

    @Test
    void updateRegionalFee_WithExistingId_ReturnsUpdatedRegionalFee() {
        // Arrange
        RegionalFee updatedRegionalFee = new RegionalFee();
        long id = 1L;
        when(regionalFeeRepository.findById(id)).thenReturn(Optional.of(new RegionalFee()));
        when(regionalFeeRepository.save(updatedRegionalFee)).thenReturn(updatedRegionalFee);

        // Act
        ResponseEntity<RegionalFee> response = controller.updateRegionalFee(id, updatedRegionalFee);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedRegionalFee, response.getBody());
    }

    @Test
    void updateRegionalFee_WithNonExistingId_ThrowsEntityNotFoundException() {
        // Arrange
        long id = 1L;
        when(regionalFeeRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> controller.updateRegionalFee(id, new RegionalFee(City.TALLINN, Vehicle.CAR, 1.0f)));
    }

    @Test
    void patchRegionalFee_WithExistingId_ReturnsPatchedRegionalFee() {
        // Arrange
        long id = 1L;
        RegionalFee existingRegionalFee = new RegionalFee(City.TALLINN, Vehicle.CAR, 1.0f);
        RegionalFee updatedRegionalFee = new RegionalFee(City.TARTU, null, null); // Update only city

        when(regionalFeeRepository.findById(id)).thenReturn(Optional.of(existingRegionalFee));
        when(regionalFeeRepository.save(any())).thenReturn(updatedRegionalFee);

        // Act
        ResponseEntity<RegionalFee> response = controller.patchRegionalFee(id, updatedRegionalFee);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedRegionalFee.getCity(), response.getBody().getCity());
        assertNull(response.getBody().getVehicle()); // Make sure vehicle remains unchanged
        assertNull(response.getBody().getFee()); // Make sure fee remains unchanged
        verify(regionalFeeRepository, times(1)).save(any());
    }

    @Test
    void patchRegionalFee_WithNonExistingId_ThrowsEntityNotFoundException() {
        // Arrange
        long id = 1L;
        when(regionalFeeRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> controller.patchRegionalFee(id, new RegionalFee(City.TALLINN, Vehicle.CAR, 1.0f)));
    }

    @Test
    void deleteRegionalFee_WithExistingId_ReturnsNoContent() {
        // Arrange
        long id = 1L;

        // Act
        ResponseEntity<Void> response = controller.deleteRegionalFee(id);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(regionalFeeRepository, times(1)).deleteById(id);
    }
}