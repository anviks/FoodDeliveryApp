package com.fujitsu.trialtask.fooddelivery.weatherfee;

import com.fujitsu.trialtask.fooddelivery.enums.Vehicle;
import com.fujitsu.trialtask.fooddelivery.enums.WeatherCondition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class WeatherFeeRepositoryTest {

    @Autowired
    private WeatherFeeRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void existsByVehicleAndConditionAndPhenomenonAndPhenomenonIsNotNull() {
        // Arrange
        Vehicle vehicle = Vehicle.CAR;
        WeatherCondition condition = WeatherCondition.PHENOMENON;
        String phenomenon = "rain";

        // Persist a WeatherFee entity with the desired conditions
        WeatherFee weatherFee = new WeatherFee();
        weatherFee.setVehicle(vehicle);
        weatherFee.setCondition(condition);
        weatherFee.setPhenomenon(phenomenon);
        entityManager.persist(weatherFee);
        entityManager.flush(); // Flush to ensure the entity is persisted

        // Act
        boolean exists = repository.existsByVehicleAndConditionAndPhenomenonAndPhenomenonIsNotNull(vehicle, condition, phenomenon);

        // Assert
        assertTrue(exists);
    }

    @Test
    void findLessStrictHigherPhenomenonFee_WithMoreStrictLowerPhenomenonFee_ReturnsWeatherFeeId() {
        // Arrange
        Vehicle vehicle = Vehicle.CAR;
        WeatherCondition condition = WeatherCondition.PHENOMENON;
        String phenomenon = "rainy";
        Float fee = 1.0f;

        // Persist a WeatherFee entity with the desired conditions
        WeatherFee weatherFee = new WeatherFee(vehicle, condition, "rain", 1.5f);
        entityManager.persist(weatherFee);
        entityManager.flush(); // Flush to ensure the entity is persisted

        // Act
        Long feeId = repository.findLessStrictHigherPhenomenonFee(vehicle, condition, phenomenon, fee);

        // Assert
        assertEquals(weatherFee.getId(), feeId);
    }

    @Test
    void findLessStringHigherPhenomenonFee_WithMoreStrictHigherPhenomenonFee_ReturnsNull() {
        // Arrange
        Vehicle vehicle = Vehicle.CAR;
        WeatherCondition condition = WeatherCondition.PHENOMENON;
        String phenomenon = "rainy";
        Float fee = 2.0f;

        // Persist a WeatherFee entity with the desired conditions
        WeatherFee weatherFee = new WeatherFee(vehicle, condition, "rain", 1.5f);
        entityManager.persist(weatherFee);
        entityManager.flush(); // Flush to ensure the entity is persisted

        // Act
        Long feeId = repository.findLessStrictHigherPhenomenonFee(vehicle, condition, phenomenon, fee);

        // Assert
        assertNull(feeId);
    }

    @Test
    void findOverlappingRange_WithOverlappingRange_ReturnsWeatherFeeId() {
        // Arrange
        Vehicle vehicle = Vehicle.CAR;
        WeatherCondition condition = WeatherCondition.WIND_SPEED;
        Float above = 20.0f;
        Float below = 30.0f;

        // Persist a WeatherFee entity with the desired conditions
        WeatherFee weatherFee = new WeatherFee();
        weatherFee.setVehicle(vehicle);
        weatherFee.setCondition(condition);
        weatherFee.setAbove(25.0f);
        weatherFee.setBelow(35.0f);
        entityManager.persist(weatherFee);
        entityManager.flush(); // Flush to ensure the entity is persisted

        // Act
        Long overlappingId = repository.findOverlappingRange(vehicle, condition, above, below);

        // Assert
        assertEquals(weatherFee.getId(), overlappingId);
    }

    @Test
    void findOverlappingRange_WithRangeEndsAtExistingRangeStart_ReturnsNull() {
        // Arrange
        Vehicle vehicle = Vehicle.CAR;
        WeatherCondition condition = WeatherCondition.WIND_SPEED;
        Float above = 20.0f;
        Float below = 25.0f;

        // Persist a WeatherFee entity with the desired conditions
        WeatherFee weatherFee = new WeatherFee();
        weatherFee.setVehicle(vehicle);
        weatherFee.setCondition(condition);
        weatherFee.setAbove(below);
        weatherFee.setBelow(35.0f);
        entityManager.persist(weatherFee);
        entityManager.flush(); // Flush to ensure the entity is persisted

        // Act
        Long overlappingId = repository.findOverlappingRange(vehicle, condition, above, below);

        // Assert
        assertNull(overlappingId);
    }
}
