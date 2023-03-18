package com.trialtask.fooddeliveryapp;


import com.trialtask.fooddeliveryapp.weather.WeatherData;

import java.sql.*;
import java.util.Properties;


@SuppressWarnings("NonAsciiCharacters")
public class DeliveryFeeCalculator {

    public enum Vehicle {CAR, SCOOTER, BIKE}
    public enum City {TALLINN, TARTU, PÄRNU}

    private final Vehicle vehicle;
    private final City city;

    public DeliveryFeeCalculator(City city, Vehicle vehicle) {
        this.city = city;
        this.vehicle = vehicle;
    }

    public float calculate() {
        float regionalFee = 0;

        switch (city) {
            case TALLINN -> {
                switch (vehicle) {
                    case CAR -> regionalFee = 4;
                    case SCOOTER -> regionalFee = 3.5f;
                    case BIKE -> regionalFee = 3;
                }
            }
            case TARTU -> {
                switch (vehicle) {
                    case CAR -> regionalFee = 3.5f;
                    case SCOOTER -> regionalFee = 3;
                    case BIKE -> regionalFee = 2.5f;
                }
            }
            case PÄRNU -> {
                switch (vehicle) {
                    case CAR -> regionalFee = 3;
                    case SCOOTER -> regionalFee = 2.5f;
                    case BIKE -> regionalFee = 2;
                }
            }
        }

        WeatherData latestWeatherData = getLatestWeatherData(city);
        return calculateWeatherFee(regionalFee, latestWeatherData);
    }

    private float calculateWeatherFee(float fee, WeatherData latestWeatherData) {
        float airTemperature = latestWeatherData.getAirTemperature();
        float windSpeed = latestWeatherData.getWindSpeed();
        String phenomenon = latestWeatherData.getPhenomenon();

        if (vehicle == Vehicle.BIKE || vehicle == Vehicle.SCOOTER) {
            if (airTemperature < -10) {
                fee += 1;
            } else if (airTemperature <= 0) {
                fee += 0.5;
            }

            if (phenomenon != null) {
                if (phenomenon.contains("snow") || phenomenon.contains("sleet")) {
                    fee += 1;
                } else if (phenomenon.contains("rain") || phenomenon.contains("shower")) {
                    fee += 0.5;
                } else if (phenomenon.equals("Glaze")
                        || phenomenon.equals("Hail")
                        || phenomenon.contains("Thunder")) {
                    return -1;
                }
            }
        }

        if (vehicle == Vehicle.BIKE) {
            if (windSpeed > 20) {
                return -1;
            }

            if (windSpeed >= 10) {
                fee += 0.5;
            }
        }

        return fee;
    }

    private WeatherData getLatestWeatherData(City city) {
        WeatherData latestData = new WeatherData();
        Properties appProperties = ReadProperties.getProperties();

        String weatherDataSource = appProperties.getProperty("spring.datasource.url");
        String username = appProperties.getProperty("spring.datasource.username", "");
        String password = appProperties.getProperty("spring.datasource.password", "");

        try {
            Connection connection = DriverManager.getConnection(weatherDataSource, username, password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM weather_data ORDER BY id DESC");
            System.out.println(resultSet);

            while (resultSet.next()) {
                String location = resultSet.getString("location");
                System.out.println(location);

                if (!location.toLowerCase().contains(city.name().toLowerCase())) {
                    continue;
                }

                latestData.setLocation(location);
                latestData.setId(resultSet.getLong("id"));
                latestData.setWmocode(resultSet.getInt("wmocode"));
                latestData.setAirTemperature(resultSet.getFloat("air_temperature"));
                latestData.setWindSpeed(resultSet.getFloat("wind_speed"));
                latestData.setPhenomenon(resultSet.getString("phenomenon"));
                latestData.setTimestamp(resultSet.getLong("timestamp"));
                break;
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return latestData;
    }
}
