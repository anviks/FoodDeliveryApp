package com.fujitsu.trialtask.fooddelivery.init;

import com.fujitsu.trialtask.fooddelivery.enums.City;
import com.fujitsu.trialtask.fooddelivery.enums.Vehicle;
import com.fujitsu.trialtask.fooddelivery.enums.WeatherCondition;
import com.fujitsu.trialtask.fooddelivery.regionalfee.RegionalFee;
import com.fujitsu.trialtask.fooddelivery.regionalfee.RegionalFeeRepository;
import com.fujitsu.trialtask.fooddelivery.weatherfee.WeatherFee;
import com.fujitsu.trialtask.fooddelivery.weatherfee.WeatherFeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Component responsible for seeding initial data into the regional fee and weather fee repositories
 * if the repositories are empty upon application startup.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final RegionalFeeRepository regionalFeeRepository;
    private final WeatherFeeRepository weatherFeeRepository;

    @Autowired
    public DataSeeder(RegionalFeeRepository regionalFeeRepository, WeatherFeeRepository weatherFeeRepository) {
        this.regionalFeeRepository = regionalFeeRepository;
        this.weatherFeeRepository = weatherFeeRepository;
    }

    /**
     * Seeds initial data into the regional fee and weather fee repositories
     * if they are empty upon application startup.
     *
     * @param args the command-line arguments (unused)
     */
    @Override
    public void run(String... args) {
        if (regionalFeeRepository.count() == 0 && weatherFeeRepository.count() == 0) {
            seedRegionalFeeRules();
            seedWeatherFeeRules();
        }
    }

    private void seedRegionalFeeRules() {
        List<RegionalFee> regionalFees = List.of(
                new RegionalFee(City.TALLINN, Vehicle.CAR, 4.0f),
                new RegionalFee(City.TALLINN, Vehicle.SCOOTER, 3.5f),
                new RegionalFee(City.TALLINN, Vehicle.BIKE, 3.0f),

                new RegionalFee(City.TARTU, Vehicle.CAR, 3.5f),
                new RegionalFee(City.TARTU, Vehicle.SCOOTER, 3.0f),
                new RegionalFee(City.TARTU, Vehicle.BIKE, 2.5f),

                new RegionalFee(City.PÄRNU, Vehicle.CAR, 3.0f),
                new RegionalFee(City.PÄRNU, Vehicle.SCOOTER, 2.5f),
                new RegionalFee(City.PÄRNU, Vehicle.BIKE, 2.0f)
        );

        regionalFeeRepository.saveAll(regionalFees);
    }

    private void seedWeatherFeeRules() {
        List<WeatherFee> weatherFees = List.of(
                new WeatherFee(Vehicle.SCOOTER, WeatherCondition.AIR_TEMPERATURE, null, -10f, 1f),
                new WeatherFee(Vehicle.BIKE, WeatherCondition.AIR_TEMPERATURE, null, -10f, 1f),
                new WeatherFee(Vehicle.SCOOTER, WeatherCondition.AIR_TEMPERATURE, -10f, 0f, .5f),
                new WeatherFee(Vehicle.BIKE, WeatherCondition.AIR_TEMPERATURE, -10f, 0f, .5f),

                new WeatherFee(Vehicle.BIKE, WeatherCondition.WIND_SPEED, 10f, 20f, .5f),
                new WeatherFee(Vehicle.BIKE, WeatherCondition.WIND_SPEED, 20f, null, null),

                new WeatherFee(Vehicle.SCOOTER, WeatherCondition.PHENOMENON, "snow", 1f),
                new WeatherFee(Vehicle.BIKE, WeatherCondition.PHENOMENON, "snow", 1f),
                new WeatherFee(Vehicle.SCOOTER, WeatherCondition.PHENOMENON, "sleet", 1f),
                new WeatherFee(Vehicle.BIKE, WeatherCondition.PHENOMENON, "sleet", 1f),
                new WeatherFee(Vehicle.SCOOTER, WeatherCondition.PHENOMENON, "rain", .5f),
                new WeatherFee(Vehicle.BIKE, WeatherCondition.PHENOMENON, "rain", .5f),
                new WeatherFee(Vehicle.SCOOTER, WeatherCondition.PHENOMENON, "shower", .5f),
                new WeatherFee(Vehicle.BIKE, WeatherCondition.PHENOMENON, "shower", .5f),
                new WeatherFee(Vehicle.SCOOTER, WeatherCondition.PHENOMENON, "glaze", null),
                new WeatherFee(Vehicle.BIKE, WeatherCondition.PHENOMENON, "glaze", null),
                new WeatherFee(Vehicle.SCOOTER, WeatherCondition.PHENOMENON, "hail", null),
                new WeatherFee(Vehicle.BIKE, WeatherCondition.PHENOMENON, "hail", null),
                new WeatherFee(Vehicle.SCOOTER, WeatherCondition.PHENOMENON, "thunder", null),
                new WeatherFee(Vehicle.BIKE, WeatherCondition.PHENOMENON, "thunder", null)
        );

        weatherFeeRepository.saveAll(weatherFees);
    }
}

