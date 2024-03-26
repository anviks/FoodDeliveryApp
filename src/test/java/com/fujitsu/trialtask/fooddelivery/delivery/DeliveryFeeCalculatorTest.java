package com.fujitsu.trialtask.fooddelivery.delivery;

import com.fujitsu.trialtask.fooddelivery.enums.City;
import com.fujitsu.trialtask.fooddelivery.enums.Vehicle;
import com.fujitsu.trialtask.fooddelivery.enums.WeatherCondition;
import com.fujitsu.trialtask.fooddelivery.exceptions.ForbiddenVehicleException;
import com.fujitsu.trialtask.fooddelivery.exceptions.UnavailableVehicleException;
import com.fujitsu.trialtask.fooddelivery.regionalfee.RegionalFee;
import com.fujitsu.trialtask.fooddelivery.regionalfee.RegionalFeeRepository;
import com.fujitsu.trialtask.fooddelivery.weatherdata.WeatherData;
import com.fujitsu.trialtask.fooddelivery.weatherdata.WeatherDataRepository;
import com.fujitsu.trialtask.fooddelivery.weatherfee.WeatherFee;
import com.fujitsu.trialtask.fooddelivery.weatherfee.WeatherFeeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DeliveryFeeCalculatorTest {
    @Mock
    private WeatherDataRepository weatherDataRepository;

    @Mock
    private RegionalFeeRepository regionalFeeRepository;

    @Mock
    private WeatherFeeRepository weatherFeeRepository;

    @InjectMocks
    private DeliveryFeeCalculator calculator;


    @Test
    void calculate_WhenOnlyRegionalFeeProvided_ShouldReturnRegionalFee() {
        // Arrange
        float expectedFee = 13.2f;
        mockWeatherData(-40.0f, 30.0f, "snow thunder shower hailstorm");
        mockRegionalFeeOf(expectedFee);
        when(weatherFeeRepository.findAllByVehicle(any())).thenReturn(List.of());

        // Act
        float result = calculator.calculate(City.TALLINN, Vehicle.CAR);

        // Assert
        assertEquals(expectedFee, result);
    }

    @Test
    void calculate_WhenOnlyPhenomenonFeeProvided_ShouldReturnPhenomenonFee() {
        // Arrange
        float expectedFee = 16.37f;
        mockWeatherData(20.0f, 5.0f, "some random weather");
        mockRegionalFeeOf(0.0f);
        WeatherFee fee = new WeatherFee(Vehicle.CAR, WeatherCondition.PHENOMENON, "random", expectedFee);
        when(weatherFeeRepository.findAllByVehicle(any())).thenReturn(List.of(fee));

        // Act
        float result = calculator.calculate(City.TALLINN, Vehicle.CAR);

        // Assert
        assertEquals(expectedFee, result);
    }

    @Test
    void calculate_WhenOnlyAirTemperatureFeeProvided_ShouldReturnAirTemperatureFee() {
        // Arrange
        mockWeatherData(20.0f, 5.0f, "some random weather");
        mockRegionalFeeOf(0.0f);
        WeatherFee fee = new WeatherFee(Vehicle.CAR, WeatherCondition.AIR_TEMPERATURE, 10.0f, null, 17.4f);
        when(weatherFeeRepository.findAllByVehicle(any())).thenReturn(List.of(fee));

        // Act
        float result = calculator.calculate(City.TALLINN, Vehicle.CAR);

        // Assert
        assertEquals(17.4f, result);
    }

    @Test
    void calculate_WhenOnlyWindSpeedFeeProvided_ShouldReturnWindSpeedFee() {
        // Arrange
        mockWeatherData(20.0f, 5.0f, "some random weather");
        mockRegionalFeeOf(0.0f);
        WeatherFee fee = new WeatherFee(Vehicle.CAR, WeatherCondition.WIND_SPEED, null, 10.0f, 19.1f);
        when(weatherFeeRepository.findAllByVehicle(any())).thenReturn(List.of(fee));

        // Act
        float result = calculator.calculate(City.TALLINN, Vehicle.CAR);

        // Assert
        assertEquals(19.1f, result);
    }

    @Test
    void calculate_WhenMultipleFeesProvided_ShouldReturnSumOfFees() {
        // Arrange
        List<Float> fees = List.of(16.3f, 17.4f, 19.1f, 10.0f, 20.0f, 5.0f, 13.2f, 18.0f);
        mockWeatherData(20.0f, 5.0f, "some random weather");
        mockRegionalFeeOf(fees.get(0));

        WeatherFee fee1 = new WeatherFee(Vehicle.CAR, WeatherCondition.AIR_TEMPERATURE, null, 10.0f, fees.get(1));
        WeatherFee fee2 = new WeatherFee(Vehicle.CAR, WeatherCondition.AIR_TEMPERATURE, 10.0f, 15.0f, null);
        WeatherFee fee3 = new WeatherFee(Vehicle.CAR, WeatherCondition.AIR_TEMPERATURE, 15.0f, null, fees.get(2));
        WeatherFee fee4 = new WeatherFee(Vehicle.CAR, WeatherCondition.WIND_SPEED, null, 0.0f, fees.get(3));
        WeatherFee fee5 = new WeatherFee(Vehicle.CAR, WeatherCondition.WIND_SPEED, 0.0f, 10.0f, fees.get(4));
        WeatherFee fee6 = new WeatherFee(Vehicle.CAR, WeatherCondition.WIND_SPEED, 10.0f, null, fees.get(5));
        WeatherFee fee7 = new WeatherFee(Vehicle.CAR, WeatherCondition.PHENOMENON, "nomatch", fees.get(6));
        WeatherFee fee8 = new WeatherFee(Vehicle.CAR, WeatherCondition.PHENOMENON, "random", fees.get(7));

        when(weatherFeeRepository.findAllByVehicle(any())).thenReturn(List.of(fee1, fee2, fee3, fee4, fee5, fee6, fee7, fee8));

        // Act
        float result = calculator.calculate(City.TALLINN, Vehicle.CAR);

        // Assert
        float expectedFee = fees.get(0) + fees.get(2) + fees.get(4) + fees.get(7);
        expectedFee = Math.round(expectedFee * 100) / 100.0f; // Round to 2 decimal places (as in the calculator)
        assertEquals(expectedFee, result);
    }

    @Test
    void calculate_WhenVehicleIsForbiddenDueToWeather_ShouldThrowException() {
        // Arrange
        mockWeatherData(20.0f, 5.0f, "some random weather");
        mockRegionalFeeOf(0.0f);
        WeatherFee fee = new WeatherFee(Vehicle.BIKE, WeatherCondition.AIR_TEMPERATURE, 10.0f, null, null);
        when(weatherFeeRepository.findAllByVehicle(any())).thenReturn(List.of(fee));

        // Act & Assert
        assertThrows(ForbiddenVehicleException.class, () -> calculator.calculate(City.TALLINN, Vehicle.CAR));
    }

    @ParameterizedTest
    @ValueSource(strings = {"sunny", "SUNNY", "SunNy", "suNnY"})
    void calculate_PhenomenonComparisonIsCaseInsensitive_ShouldReturnFee(String phenomenon) {
        // Arrange
        mockWeatherData(2.0f, 5.0f, phenomenon);
        mockRegionalFeeOf(5.0f);
        WeatherFee fee = new WeatherFee(Vehicle.CAR, WeatherCondition.PHENOMENON, "sunny", 10.0f);
        when(weatherFeeRepository.findAllByVehicle(any())).thenReturn(List.of(fee));

        // Act
        float result = calculator.calculate(City.TALLINN, Vehicle.CAR);

        // Assert
        assertEquals(15.0f, result);
    }

    @Test
    void calculate_WithMultipleMatchingPhenomenonFees_ShouldReturnMaxFee() {
        // Arrange
        mockWeatherData(20.0f, 5.0f, "moderate snow shower");
        mockRegionalFeeOf(0.0f);

        WeatherFee fee1 = new WeatherFee();
        fee1.setCondition(WeatherCondition.PHENOMENON);
        fee1.setPhenomenon("snow");
        fee1.setFee(15.0f);

        WeatherFee fee2 = new WeatherFee();
        fee2.setCondition(WeatherCondition.PHENOMENON);
        fee2.setPhenomenon("shower");
        fee2.setFee(10.0f);

        when(weatherFeeRepository.findAllByVehicle(any())).thenReturn(List.of(fee1, fee2));

        // Act
        float result = calculator.calculate(City.TALLINN, Vehicle.CAR);

        // Assert
        assertEquals(15.0f, result);
    }

    @Test
    void calculate_WithEdgeCaseWeatherConditions_ShouldReturnLargerFee() {
        // Arrange
        mockWeatherData(20.0f, 5.0f, "moderate snow shower");
        mockRegionalFeeOf(0.0f);

        WeatherFee fee1 = new WeatherFee();
        fee1.setCondition(WeatherCondition.WIND_SPEED);
        fee1.setAbove(0.0f);
        fee1.setBelow(5.0f);
        fee1.setFee(14.8f);

        WeatherFee fee2 = new WeatherFee();
        fee2.setCondition(WeatherCondition.WIND_SPEED);
        fee2.setAbove(5.0f);
        fee2.setBelow(10.0f);
        fee2.setFee(27.3f);

        when(weatherFeeRepository.findAllByVehicle(any())).thenReturn(List.of(fee1, fee2));

        // Act
        float result = calculator.calculate(City.TALLINN, Vehicle.CAR);

        // Verify
        assertEquals(27.3f, result);
    }

    @Test
    void calculate_WithNoRegionalFee_ShouldThrowException() {
        // Arrange
        when(regionalFeeRepository.findByCityAndVehicle(any(), any())).thenReturn(null);

        // Act & Assert
        assertThrows(UnavailableVehicleException.class, () -> calculator.calculate(City.TALLINN, Vehicle.CAR));
    }

    // Helper methods for mocking

    private void mockRegionalFeeOf(float fee) {
        RegionalFee regionalFee = new RegionalFee();
        regionalFee.setFee(fee);
        when(regionalFeeRepository.findByCityAndVehicle(any(), any())).thenReturn(regionalFee);
    }

    private void mockWeatherData(float airTemperature, float windSpeed, String phenomenon) {
        WeatherData weatherData = new WeatherData();
        weatherData.setAirTemperature(airTemperature);
        weatherData.setWindSpeed(windSpeed);
        weatherData.setPhenomenon(phenomenon);
        when(weatherDataRepository.findFirstByCityOrderByTimestampDesc(any())).thenReturn(weatherData);
    }
}
