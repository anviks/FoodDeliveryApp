package com.fujitsu.trialtask.fooddelivery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fujitsu.trialtask.fooddelivery.enums.City;
import com.fujitsu.trialtask.fooddelivery.enums.Vehicle;
import com.fujitsu.trialtask.fooddelivery.enums.WeatherCondition;
import com.fujitsu.trialtask.fooddelivery.init.DataSeeder;
import com.fujitsu.trialtask.fooddelivery.regionalfee.RegionalFeeRepository;
import com.fujitsu.trialtask.fooddelivery.weatherdata.WeatherData;
import com.fujitsu.trialtask.fooddelivery.weatherdata.WeatherDataRepository;
import com.fujitsu.trialtask.fooddelivery.weatherfee.WeatherFee;
import com.fujitsu.trialtask.fooddelivery.weatherfee.WeatherFeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class FoodDeliveryApplicationIntegrationTests {

    @Autowired
    private RegionalFeeRepository regionalFeeRepository;

    @Autowired
    private WeatherFeeRepository weatherFeeRepository;

    @Autowired
    private WeatherDataRepository weatherDataRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        DataSeeder dataSeeder = new DataSeeder(regionalFeeRepository, weatherFeeRepository);
        dataSeeder.run();
    }

    @Test
    public void testDeliveryControllerEndpoint() throws Exception {
        mockMvc.perform(get("/api/delivery/{city}", City.TALLINN.name())
                        .param("vehicle", Vehicle.BIKE.name())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value(City.TALLINN.name()))
                .andExpect(jsonPath("$.vehicle").value(Vehicle.BIKE.name()))
                .andExpect(jsonPath("$.fee").isNumber())
                .andDo(print());
    }

    @Test
    public void dataSeeder_ShouldSeedCorrectFees() throws Exception {
        weatherDataRepository.deleteAll();
        assertEquals(9, regionalFeeRepository.count());
        assertEquals(20, weatherFeeRepository.count());
        assertEquals(0, weatherDataRepository.count());

        WeatherData weatherData = new WeatherData(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC), City.TALLINN, 0, "Overcast", 10.0f, 5.0f);
        weatherDataRepository.save(weatherData);

        mockMvc.perform(get("/api/delivery/{city}", City.TALLINN.name())
                .param("vehicle", Vehicle.BIKE.name())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value(City.TALLINN.name()))
                .andExpect(jsonPath("$.vehicle").value(Vehicle.BIKE.name()))
                .andExpect(jsonPath("$.fee").value(3));
    }

    @Test
    public void weatherFeeControllerEndpoint_ShouldReturnWeatherFee() throws Exception {
        WeatherFee weatherFee = new WeatherFee(Vehicle.BIKE, WeatherCondition.PHENOMENON, "Non-existent phenomenon", 10.0f);
        AtomicReference<Long> id = new AtomicReference<>();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/weather-fees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(weatherFee)))
                .andExpect(status().isCreated())
                .andDo(result -> {
                    String content = result.getResponse().getContentAsString();
                    WeatherFee createdWeatherFee = objectMapper.readValue(content, WeatherFee.class);
                    id.set(createdWeatherFee.getId());
                    assertNotNull(id);
                })
                .andDo(print());

        mockMvc.perform(get("/api/weather-fees/{id}", id.get())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.get()))
                .andExpect(jsonPath("$.vehicle").value(weatherFee.getVehicle().name()))
                .andExpect(jsonPath("$.condition").value(weatherFee.getCondition().name()))
                .andExpect(jsonPath("$.phenomenon").value(weatherFee.getPhenomenon()))
                .andExpect(jsonPath("$.fee").value(weatherFee.getFee()))
                .andExpect(jsonPath("$.above").doesNotExist())
                .andExpect(jsonPath("$.below").doesNotExist())
                .andDo(print());

    }
}
