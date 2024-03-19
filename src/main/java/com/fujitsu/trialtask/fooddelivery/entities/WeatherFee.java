package com.fujitsu.trialtask.fooddelivery.entities;


import com.fujitsu.trialtask.fooddelivery.enums.Vehicle;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class WeatherFee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Vehicle vehicle;

    @ElementCollection
    @CollectionTable(name = "air_temperature_condition")
    private List<WeatherRangeCondition> airTemperatureConditions;

    @ElementCollection
    @CollectionTable(name = "wind_speed_condition")
    private List<WeatherRangeCondition> windSpeedConditions;

    @ElementCollection
    @CollectionTable(name = "weather_phenomenon_condition")
    @AttributeOverrides({
            @AttributeOverride(name = "contains", column = @Column(name = "phenomenon_keyword")),
            @AttributeOverride(name = "fee", column = @Column(name = "phenomenon_fee"))
    })
    private List<WeatherPhenomenonCondition> weatherPhenomenonConditions;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public List<WeatherRangeCondition> getAirTemperatureConditions() {
        return airTemperatureConditions;
    }

    public void setAirTemperatureConditions(List<WeatherRangeCondition> airTemperatureConditions) {
        this.airTemperatureConditions = airTemperatureConditions;
    }

    public List<WeatherRangeCondition> getWindSpeedConditions() {
        return windSpeedConditions;
    }

    public void setWindSpeedConditions(List<WeatherRangeCondition> windSpeedConditions) {
        this.windSpeedConditions = windSpeedConditions;
    }

    public List<WeatherPhenomenonCondition> getWeatherPhenomenonConditions() {
        return weatherPhenomenonConditions;
    }

    public void setWeatherPhenomenonConditions(List<WeatherPhenomenonCondition> weatherPhenomenonConditions) {
        this.weatherPhenomenonConditions = weatherPhenomenonConditions;
    }
}


