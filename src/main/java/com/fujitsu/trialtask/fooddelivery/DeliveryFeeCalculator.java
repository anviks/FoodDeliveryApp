package com.fujitsu.trialtask.fooddelivery;


import com.fujitsu.trialtask.fooddelivery.enums.City;
import com.fujitsu.trialtask.fooddelivery.enums.Vehicle;
import com.fujitsu.trialtask.fooddelivery.entities.WeatherData;
import com.fujitsu.trialtask.fooddelivery.repositories.WeatherDataRepository;

import java.util.HashMap;
import java.util.Map;


/**
 * This class calculates the delivery fee based on the city, vehicle type and weather condition.
 */
public class DeliveryFeeCalculator {
    // Regional delivery fees
    private static final Map<City, Map<Vehicle, Float>> REGIONAL_FEES = new HashMap<>();

    static {
        REGIONAL_FEES.put(City.TALLINN, Map.of(Vehicle.CAR, 4f, Vehicle.SCOOTER, 3.5f, Vehicle.BIKE, 3f));
        REGIONAL_FEES.put(City.TARTU, Map.of(Vehicle.CAR, 3.5f, Vehicle.SCOOTER, 3f, Vehicle.BIKE, 2.5f));
        REGIONAL_FEES.put(City.PÄRNU, Map.of(Vehicle.CAR, 3f, Vehicle.SCOOTER, 2.5f, Vehicle.BIKE, 2f));
    }

    private final Vehicle vehicle;
    private final City city;
    private final WeatherDataRepository weatherDataRepository;

    /**
     * Constructs a new {@code DeliveryFeeCalculator} instance with the specified city and vehicle.
     *
     * @param city                  the city for which the delivery fee is being calculated
     * @param vehicle               the vehicle for which the delivery fee is being calculated
     * @param weatherDataRepository the repository for weather data
     */
    public DeliveryFeeCalculator(City city, Vehicle vehicle, WeatherDataRepository weatherDataRepository) {
        this.city = city;
        this.vehicle = vehicle;
        this.weatherDataRepository = weatherDataRepository;
    }

    /**
     * Calculates the delivery fee based on the city, vehicle type and weather condition.
     *
     * @return the delivery fee or -1 if the weather conditions are not suitable for delivery.
     */
    public float calculate() {
        float regionalFee = getRegionalFee();
        float weatherFee = calculateWeatherFee();
        return weatherFee == -1 ? -1 : regionalFee + weatherFee;
    }

    private float getRegionalFee() {
        return REGIONAL_FEES.get(city).get(vehicle);
    }

    private float calculateWeatherFee() {
        WeatherData latestWeatherData = getLatestWeatherData();
        float fee = 0;

        float airTemperature = latestWeatherData.getAirTemperature();
        float windSpeed = latestWeatherData.getWindSpeed();
        String phenomenon = latestWeatherData.getPhenomenon();

        if (vehicle == Vehicle.BIKE || vehicle == Vehicle.SCOOTER) {
            if (airTemperature < -10) {
                fee += 1;
            } else if (airTemperature <= 0) {
                fee += 0.5F;
            }

            if (phenomenon != null) {
                phenomenon = phenomenon.toLowerCase();

                if (phenomenon.contains("snow") || phenomenon.contains("sleet")) {
                    fee += 1;
                } else if (phenomenon.contains("rain") || phenomenon.contains("shower")) {
                    fee += 0.5F;
                } else if (phenomenon.equals("glaze")
                        || phenomenon.equals("hail")
                        || phenomenon.contains("thunder")) {
                    return -1;
                }
            }
        }

        if (vehicle == Vehicle.BIKE) {
            if (windSpeed > 20) {
                return -1;
            }

            if (windSpeed >= 10) {
                fee += 0.5F;
            }
        }

        return fee;
    }

    /**
     * Returns the latest weather data for the specified city.
     *
     * @param city the city for which to retrieve the latest weather data.
     *
     * @return the latest weather data for the specified city.
     */
    public WeatherData getLatestWeatherData(City city) {
        return this.weatherDataRepository.findFirstByLocationContainingIgnoreCaseOrderByTimestampDesc(city.name());
    }

    /**
     * Returns the latest weather data for the city associated with this instance.
     *
     * @return the latest weather data for the city associated with this instance.
     */
    public WeatherData getLatestWeatherData() {
        return getLatestWeatherData(city);
    }
}
