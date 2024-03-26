package com.fujitsu.trialtask.fooddelivery.delivery;


import com.fujitsu.trialtask.fooddelivery.enums.City;
import com.fujitsu.trialtask.fooddelivery.enums.Vehicle;
import com.fujitsu.trialtask.fooddelivery.init.DataSeeder;
import com.fujitsu.trialtask.fooddelivery.weatherdata.WeatherData;
import com.fujitsu.trialtask.fooddelivery.weatherdata.WeatherDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Integration tests for the {@link DeliveryController} class.
 * This class tests the functionality of the delivery fee calculation API controller
 * by performing integration tests with the entire application context, including
 * database interaction and HTTP request/response handling. It primarily focuses on
 * testing the behavior of the {@link DeliveryController} class with respect to the
 * data seeded by the {@link DataSeeder}.
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class DeliveryControllerIT {

    private static final String CONTROLLER_URL = "/api/delivery/{city}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DataSeeder dataSeeder;

    @Autowired
    private WeatherDataRepository weatherDataRepository;

    @BeforeEach
    void setUp(final TestInfo testInfo) {
        if (testInfo.getTags().contains("no-setup")) {
            return;
        }

        weatherDataRepository.deleteAll();
        dataSeeder.run();
    }

    @Tag("no-setup")
    @Test
    void getDeliveryFee_WithValidCityAndVehicle_ShouldReturnDeliveryFee() throws Exception {
        // Arrange
        String cityName = "TALLINN";
        String vehicleName = "CAR";

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_URL, cityName)
                        .param("vehicle", vehicleName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value(cityName))
                .andExpect(jsonPath("$.vehicle").value(vehicleName))
                .andExpect(jsonPath("$.fee").exists());
    }

    @Tag("no-setup")
    @Test
    void getDeliveryFee_WithInvalidCity_ShouldReturnNotFound() throws Exception {
        // Arrange
        String cityName = "UNKNOWN";
        String vehicleName = "CAR";

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_URL, cityName)
                        .param("vehicle", vehicleName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Tag("no-setup")
    @Test
    void getDeliveryFee_WithInvalidVehicle_ShouldReturnNotFound() throws Exception {
        // Arrange
        String cityName = "TALLINN";
        String vehicleName = "UNKNOWN";

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_URL, cityName)
                        .param("vehicle", vehicleName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @CsvSource({
            "TALLINN, CAR, 4.0",
            "TALLINN, SCOOTER, 3.5",
            "TALLINN, BIKE, 3.0",
            "TARTU, CAR, 3.5",
            "TARTU, SCOOTER, 3.0",
            "TARTU, BIKE, 2.5",
            "PÄRNU, CAR, 3.0",
            "PÄRNU, SCOOTER, 2.5",
            "PÄRNU, BIKE, 2.0"
    })
    void getDeliveryFee_WithNoWeatherData_ShouldReturnRegionalFee(City city, Vehicle vehicle, float expectedFee) throws Exception {
        assertDeliveryFee(vehicle, city, expectedFee);
    }

    @ParameterizedTest
    @CsvSource({
            "Thunderstorm, SCOOTER",
            "Thunderstorm, BIKE",
            "Glaze, SCOOTER",
            "Glaze, BIKE",
            "Hail, SCOOTER",
            "Hail, BIKE"
    })
    void getDeliveryFee_WithVeryBadWeatherPhenomenon_WithScooterBike_ShouldReturnUnprocessableEntity(String phenomenon, Vehicle vehicle) throws Exception {
        // Arrange
        City city = City.TALLINN;
        mockWeatherData(city, phenomenon);

        // Act & Assert
        assertUnprocessable(city, vehicle);
    }

    @ParameterizedTest
    @CsvSource({
            "Thunderstorm, CAR, 4.0",
            "Glaze, CAR, 4.0",
            "Hail, CAR, 4.0",
    })
    void getDeliveryFee_WithVeryBadWeatherPhenomenon_WithCar_ShouldReturnRegionalFee(String phenomenon, Vehicle vehicle, float expectedFee) throws Exception {
        // Arrange
        City city = City.TALLINN;
        mockWeatherData(city, phenomenon);

        // Act & Assert
        assertDeliveryFee(vehicle, city, expectedFee);
    }


    @ParameterizedTest
    @CsvSource({
            "Moderate snow shower, SCOOTER, 4.5",
            "Light snowfall, BIKE, 4.0",
            "Moderate sleet, SCOOTER, 4.5",
            "Light sleet, BIKE, 4.0",
            "Light rain, SCOOTER, 4.0",
            "Heavy rain, BIKE, 3.5",
            "Moderate shower, SCOOTER, 4.0",
            "Light shower, BIKE, 3.5"
    })
    void getDeliveryFee_WithBadWeatherPhenomenon_WithScooterBike_ShouldReturnHigherFee(String phenomenon, Vehicle vehicle, float expectedFee) throws Exception {
        // Arrange
        City city = City.TALLINN;
        mockWeatherData(city, phenomenon);

        // Act & Assert
        assertDeliveryFee(vehicle, city, expectedFee);
    }

    @ParameterizedTest
    @CsvSource({
            "Moderate snow shower, CAR, 4.0",
            "Light snowfall, CAR, 4.0",
            "Moderate sleet, CAR, 4.0",
            "Light sleet, CAR, 4.0",
            "Light rain, CAR, 4.0",
            "Heavy rain, CAR, 4.0",
            "Moderate shower, CAR, 4.0",
            "Light shower, CAR, 4.0"
    })
    void getDeliveryFee_WithBadWeatherPhenomenon_WithCar_ShouldReturnRegionalFee(String phenomenon, Vehicle vehicle, float expectedFee) throws Exception {
        // Arrange
        City city = City.TALLINN;
        mockWeatherData(city, phenomenon);

        // Act & Assert
        assertDeliveryFee(vehicle, city, expectedFee);
    }

    @Test
    void getDeliveryFee_WithHighWindSpeed_WithBike_ShouldReturnHigherFee() throws Exception {
        // Arrange
        City city = City.TALLINN;
        mockWeatherData(city, 10.0f, 15.0f);

        // Act & Assert
        assertDeliveryFee(Vehicle.BIKE, city, 3.5f);
    }

    @Test
    void getDeliveryFee_WithVeryHighWindSpeed_WithBike_ShouldReturnUnprocessableEntity() throws Exception {
        // Arrange
        City city = City.TALLINN;
        mockWeatherData(city, 10.0f, 25.0f);

        // Act & Assert
        assertUnprocessable(city, Vehicle.BIKE);
    }

    @ParameterizedTest
    @CsvSource({
            "15.0, SCOOTER, 3.5",
            "15.0, CAR, 4.0",
            "20.0, SCOOTER, 3.5",
            "20.0, CAR, 4.0",
            "25.0, SCOOTER, 3.5",
            "25.0, CAR, 4.0"
    })
    void getDeliveryFee_WithHighWindSpeed_WithScooterCar_ShouldReturnRegionalFee(float windSpeed, Vehicle vehicle, float expectedFee) throws Exception {
        // Arrange
        City city = City.TALLINN;
        mockWeatherData(city, 10.0f, windSpeed);

        // Act & Assert
        assertDeliveryFee(vehicle, city, expectedFee);
    }

    @ParameterizedTest
    @CsvSource({
            "-15.0, SCOOTER, 4.5",
            "-15.0, BIKE, 4.0",
            "-10.0, SCOOTER, 4.5",
            "-10.0, BIKE, 4.0",
            "-5.0, SCOOTER, 4.0",
            "-5.0, BIKE, 3.5"
    })
    void getDeliveryFee_WithLowTemperature_WithScooterBike_ShouldReturnHigherFee(float temperature, Vehicle vehicle, float expectedFee) throws Exception {
        // Arrange
        City city = City.TALLINN;
        mockWeatherData(city, temperature, 5.0f);

        // Act & Assert
        assertDeliveryFee(vehicle, city, expectedFee);
    }

    @ParameterizedTest
    @CsvSource({
            "-15.0, CAR, 4.0",
            "-10.0, CAR, 4.0",
            "-5.0, CAR, 4.0"
    })
    void getDeliveryFee_WithLowTemperature_WithCar_ShouldReturnRegionalFee(float temperature, Vehicle vehicle, float expectedFee) throws Exception {
        // Arrange
        City city = City.TALLINN;
        mockWeatherData(city, temperature, 5.0f);

        // Act & Assert
        assertDeliveryFee(vehicle, city, expectedFee);
    }

    private void assertUnprocessable(City city, Vehicle vehicle) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_URL, city.name())
                        .param("vehicle", vehicle.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());
    }

    private void assertDeliveryFee(Vehicle vehicle, City city, float expectedFee) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(CONTROLLER_URL, city.name())
                        .param("vehicle", vehicle.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fee").value(expectedFee));
    }

    private void mockWeatherData(City city, float airTemperature, float windSpeed) {
        mockWeatherData(city, "someRandomPhenomenon", airTemperature, windSpeed);
    }

    private void mockWeatherData(City city, String phenomenon) {
        mockWeatherData(city, phenomenon, 10.0f, 5.0f);
    }

    private void mockWeatherData(City city, String phenomenon, float airTemperature, float windSpeed) {
        WeatherData badWeather = new WeatherData(1234L, city, 0, phenomenon, airTemperature, windSpeed);
        weatherDataRepository.save(badWeather);
    }
}
