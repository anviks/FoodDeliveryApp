package com.trialtask.fooddeliveryapp;

import com.trialtask.fooddeliveryapp.enums.City;
import com.trialtask.fooddeliveryapp.enums.Vehicle;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * This class represents the delivery fee calculation API controller for the food delivery application.
 * It provides an HTTP endpoint for calculating the delivery fee for a given city and vehicle type.
 * The controller takes the city and vehicle type as input, validates them and returns a fee message or an error message.
 */
@RestController
@RequestMapping("/api/delivery")
public class Controller {

    /**
     * Calculates the delivery fee for a given city and vehicle type.
     *
     * @param cityString    the name of the city for the delivery
     * @param vehicleString the type of vehicle for the delivery
     * @return a String message indicating the fee for the delivery,
     * or an error message if the city or vehicle type is not supported
     * or if the weather conditions aren't suitable for delivery
     */
    @GetMapping
    public String getFee(@RequestParam("city") String cityString,
                         @RequestParam("vehicle") String vehicleString) {
        City city;
        Vehicle vehicle;

        List<String> allowedCityNames = Arrays.stream(City.values())
                .map(c -> capitalise(c.name()))
                .toList();

        List<String> allowedVehicles = Arrays.stream(Vehicle.values())
                .map(c -> c.name().toLowerCase())
                .toList();

        if (allowedCityNames.contains(capitalise(cityString))) {
            city = City.valueOf(cityString.toUpperCase());
        } else {
            return "Unfortunately, we only operate in " + String.join(", ", allowedCityNames);
        }

        if (allowedVehicles.contains(vehicleString.toLowerCase())) {
            vehicle = Vehicle.valueOf(vehicleString.toUpperCase());
        } else {
            return "Unfortunately, we can only deliver by " + String.join(", ", allowedVehicles);
        }

        DeliveryFeeCalculator calculator = new DeliveryFeeCalculator(city, vehicle);
        float fee = calculator.calculate();

        if (fee == -1) {
            return "Usage of selected vehicle type is forbidden";
        }

        return "The fee to deliver food in "
                + capitalise(city.name())
                + " by "
                + vehicle.name().toLowerCase()
                + " at this time, costs "
                + calculator.calculate()
                + "€.";
    }

    private String capitalise(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }
}

