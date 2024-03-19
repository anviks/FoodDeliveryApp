package com.fujitsu.trialtask.fooddelivery.controllers;

import com.fujitsu.trialtask.fooddelivery.DeliveryFeeCalculator;
import com.fujitsu.trialtask.fooddelivery.enums.City;
import com.fujitsu.trialtask.fooddelivery.enums.Vehicle;
import com.fujitsu.trialtask.fooddelivery.repositories.WeatherDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents the delivery fee calculation API controller for the food delivery application.
 * It provides an HTTP endpoint for calculating the delivery fee for a given city and vehicle type.
 * The controller takes the city and vehicle type as input, validates them and returns a fee message or an error message.
 */
@RestController
public class DeliveryController {
    private final WeatherDataRepository weatherDataRepository;

    @Autowired
    public DeliveryController(WeatherDataRepository weatherDataRepository) {
        this.weatherDataRepository = weatherDataRepository;
    }

    @GetMapping("/api/cities")
    public ResponseEntity<List<String>> getAllCities() {
        return ResponseEntity.ok(Arrays.stream(City.values())
                .map(City::toString)
                .toList());
    }

    @GetMapping("/api/vehicles")
    public ResponseEntity<List<String>> getAllVehicles() {
        return ResponseEntity.ok(Arrays.stream(Vehicle.values())
                .map(Vehicle::toString)
                .toList());
    }

    @GetMapping("/api/delivery/{city}")
    public ResponseEntity<Object> getDeliveryFee(@PathVariable("city") String cityName,
                                                 @RequestParam("vehicle") String vehicleName) {
        City city = City.getCityByName(cityName);
        Vehicle vehicle = Vehicle.getVehicleByName(vehicleName);

        if (city == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("City not found");
        }

        if (vehicle == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vehicle not found");
        }

        DeliveryFeeCalculator calculator = new DeliveryFeeCalculator(city, vehicle, this.weatherDataRepository);
        float fee = calculator.calculate();

        if (fee == -1) {
            // TODO: change error message
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usage of selected vehicle type is forbidden");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("city", city.toString());
        response.put("vehicle", vehicle.toString());
        response.put("fee", fee);

        return ResponseEntity.ok(response);
    }
}

