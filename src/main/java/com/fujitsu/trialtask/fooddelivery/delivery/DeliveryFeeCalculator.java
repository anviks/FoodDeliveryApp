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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;


/**
 * This class calculates the delivery fee based on the city, vehicle type and weather condition.
 */
@Component
class DeliveryFeeCalculator {
    private static final String VEHICLE_FORBIDDEN_DETAIL = "Usage of selected vehicle type (%s) is forbidden due to current weather conditions (%s)";
    private static final String VEHICLE_UNAVAILABLE_DETAIL = "The selected vehicle type (%s) is not available in the specified city (%s)";
    private final WeatherDataRepository weatherDataRepository;
    private final RegionalFeeRepository regionalFeeRepository;
    private final WeatherFeeRepository weatherFeeRepository;

    /**
     * Constructs a new {@code DeliveryFeeCalculator} instance with the specified repositories.
     *
     * @param weatherDataRepository the repository for weather data
     * @param regionalFeeRepository the repository for regional fees
     * @param weatherFeeRepository  the repository for weather fees
     */
    @Autowired
    public DeliveryFeeCalculator(WeatherDataRepository weatherDataRepository,
                                 RegionalFeeRepository regionalFeeRepository,
                                 WeatherFeeRepository weatherFeeRepository) {
        this.weatherDataRepository = weatherDataRepository;
        this.regionalFeeRepository = regionalFeeRepository;
        this.weatherFeeRepository = weatherFeeRepository;
    }

    /**
     * Calculates the total delivery fee based on the city, vehicle type, and weather condition.
     *
     * @param city    the city for delivery
     * @param vehicle the type of vehicle for delivery
     *
     * @return the calculated delivery fee
     * @throws UnavailableVehicleException if the selected vehicle type is not available in the specified city
     * @throws ForbiddenVehicleException   if the selected vehicle type is forbidden due to current weather conditions
     */
    public float calculate(City city, Vehicle vehicle) {
        RegionalFee regionalFee = regionalFeeRepository.findByCityAndVehicle(city, vehicle);
        // Presume that the vehicle is unavailable if the regional fee is not found
        if (regionalFee == null) {
            throw new UnavailableVehicleException(VEHICLE_UNAVAILABLE_DETAIL.formatted(vehicle, city));
        }
        float totalFee = regionalFee.getFee() + calculateTotalWeatherFee(city, vehicle);
        return Math.round(totalFee * 100) / 100.0f;
    }

    private float calculateTotalWeatherFee(City city, Vehicle vehicle) {
        WeatherData latestWeatherData = weatherDataRepository.findFirstByCityOrderByTimestampDesc(city);
        double[] maxFees = new double[WeatherCondition.values().length];
        List<WeatherFee> weatherFees = weatherFeeRepository.findAllByVehicle(vehicle);

        if (latestWeatherData == null) {
            return 0;
        }

        Float airTemperature = latestWeatherData.getAirTemperature();
        Float windSpeed = latestWeatherData.getWindSpeed();
        String phenomenon = latestWeatherData.getPhenomenon();

        for (WeatherFee weatherFee : weatherFees) {
            WeatherCondition condition = weatherFee.getCondition();

            boolean applies = switch (condition) {
                case AIR_TEMPERATURE -> weatherFee.appliesTo(airTemperature);
                case WIND_SPEED -> weatherFee.appliesTo(windSpeed);
                case PHENOMENON -> weatherFee.appliesTo(phenomenon);
            };

            if (applies) {
                int ordinal = condition.ordinal();
                // This is necessary to avoid two fees for the same condition
                // (for example: two fees for "snow shower" because it contains both "snow" and "shower")
                maxFees[ordinal] = Math.max(maxFees[ordinal], applyFee(weatherFee, latestWeatherData));
            }
        }

        return (float) Arrays.stream(maxFees).sum();
    }

    private float applyFee(WeatherFee weatherFee, WeatherData weatherData) {
        if (weatherFee.getFee() != null) {
            return weatherFee.getFee();
        } else {
            throw new ForbiddenVehicleException(
                    String.format(
                            VEHICLE_FORBIDDEN_DETAIL,
                            weatherFee.getVehicle(),
                            getWeatherConditionMessage(weatherFee.getCondition(), weatherData)));
        }
    }

    private String getWeatherConditionMessage(WeatherCondition condition, WeatherData weatherData) {
        return switch (condition) {
            case WIND_SPEED -> String.format("wind speed: %.1f m/s", weatherData.getWindSpeed());
            case AIR_TEMPERATURE -> String.format("air temperature: %.1f °C", weatherData.getAirTemperature());
            case PHENOMENON -> String.format("phenomenon: %s", weatherData.getPhenomenon());
        };
    }
}
