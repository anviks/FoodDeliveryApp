package com.fujitsu.trialtask.fooddelivery.delivery;

import com.fujitsu.trialtask.fooddelivery.enums.City;
import com.fujitsu.trialtask.fooddelivery.enums.Vehicle;
import com.fujitsu.trialtask.fooddelivery.helpers.EnumConverter;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the delivery fee calculation API controller for the food delivery application.
 * It provides an HTTP endpoint for calculating the delivery fee for a given city and vehicle type.
 * The controller takes the city and vehicle type as input, validates them and returns a fee message or an error message.
 */
@RestController
@RequestMapping("/api/delivery")
class DeliveryController {

    private final DeliveryFeeCalculator deliveryFeeCalculator;

    @Autowired
    public DeliveryController(DeliveryFeeCalculator deliveryFeeCalculator) {
        this.deliveryFeeCalculator = deliveryFeeCalculator;
    }

    /**
     * Retrieves the delivery fee for the specified city and vehicle type.
     *
     * @param cityName    the name of the city for delivery
     * @param vehicleName the name of the vehicle type for delivery
     *
     * @return ResponseEntity containing JSON response with the calculated delivery fee, city, and vehicle type
     * @throws EntityNotFoundException if the provided city or vehicle type is not found
     */
    @GetMapping(value = "/{city}", produces = "application/json")
    public ResponseEntity<Map<String, Object>> getDeliveryFee(
            @PathVariable("city") String cityName,
            @RequestParam("vehicle") String vehicleName) {

        City city = EnumConverter.convertStringToEnum(cityName, City.class);
        Vehicle vehicle = EnumConverter.convertStringToEnum(vehicleName, Vehicle.class);

        if (city == null) {
            throw new EntityNotFoundException("City not found");
        }

        if (vehicle == null) {
            throw new EntityNotFoundException("Vehicle not found");
        }

        float fee = deliveryFeeCalculator.calculate(city, vehicle);

        Map<String, Object> response = new HashMap<>();
        response.put("city", city.toString());
        response.put("vehicle", vehicle.toString());
        response.put("fee", fee);

        return ResponseEntity.ok(response);
    }
}

