package com.fujitsu.trialtask.fooddelivery.weatherdata;

import com.fujitsu.trialtask.fooddelivery.enums.City;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Objects;

/**
 * Represents weather data for a particular location at a particular time.
 * This class is a JPA entity and can be persisted to a database using an appropriate JPA implementation.
 */
@Entity
public class WeatherData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long timestamp;
    private City city;
    private Integer wmocode;
    private String phenomenon;
    private Float airTemperature;
    private Float windSpeed;

    /**
     * Constructs a new WeatherData object with the specified values.
     *
     * @param timestamp      the time at which the weather data was recorded, in Unix timestamp format
     * @param city           the city for which the weather data is recorded
     * @param wmocode        the World Meteorological Organization code for the location
     * @param phenomenon     the phenomenon that the weather data represents (e.g., "Overcast", "Glaze", etc.)
     * @param airTemperature the air temperature in Celsius
     * @param windSpeed      the wind speed in meters per second
     */
    public WeatherData(Long timestamp, City city, Integer wmocode, String phenomenon, Float airTemperature, Float windSpeed) {
        this.timestamp = timestamp;
        this.city = city;
        this.wmocode = wmocode;
        setPhenomenon(phenomenon);
        this.airTemperature = airTemperature;
        this.windSpeed = windSpeed;
    }

    /**
     * Default constructor required by JPA.
     */
    public WeatherData() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City location) {
        this.city = location;
    }

    public Integer getWmocode() {
        return wmocode;
    }

    public void setWmocode(Integer wmocode) {
        this.wmocode = wmocode;
    }

    public Float getAirTemperature() {
        return airTemperature;
    }

    public void setAirTemperature(Float airTemperature) {
        this.airTemperature = airTemperature;
    }

    public Float getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Float windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getPhenomenon() {
        return phenomenon;
    }

    public void setPhenomenon(String phenomenon) {
        this.phenomenon = phenomenon == null ? null : phenomenon.toLowerCase();
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Indicates whether some other object is equal to this one.
     * Equality is determined by comparing the timestamp, location, wmocode,
     * phenomenon, airTemperature, and windSpeed fields of the two objects.
     *
     * @param o the object to compare with
     *
     * @return true if this object is the same as the argument object, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeatherData data = (WeatherData) o;
        return Objects.equals(timestamp, data.timestamp)
                && Objects.equals(city, data.city)
                && Objects.equals(wmocode, data.wmocode)
                && Objects.equals(phenomenon, data.phenomenon)
                && Objects.equals(airTemperature, data.airTemperature)
                && Objects.equals(windSpeed, data.windSpeed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, city, wmocode, phenomenon, airTemperature, windSpeed);
    }

    @Override
    public String toString() {
        return "WeatherData{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", city=" + city +
                ", wmocode=" + wmocode +
                ", phenomenon='" + phenomenon + '\'' +
                ", airTemperature=" + airTemperature +
                ", windSpeed=" + windSpeed +
                '}';
    }
}
