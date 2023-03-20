package com.trialtask.fooddeliveryapp.weather;

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
    private Long timestamp = null;
    private String location = null;
    private Integer wmocode = null;
    private String phenomenon = null;
    private Float airTemperature = null;
    private Float windSpeed = null;

    /**
     * Constructs a new WeatherData object with the specified values.
     *
     * @param timestamp      the time at which the weather data was recorded, in Unix timestamp format
     * @param location       the location for which the weather data is recorded
     * @param wmocode        the World Meteorological Organization code for the location
     * @param phenomenon     the phenomenon that the weather data represents (e.g., "Overcast", "Glaze", etc.)
     * @param airTemperature the air temperature in Celsius
     * @param windSpeed      the wind speed in meters per second
     */
    public WeatherData(long timestamp, String location, int wmocode, String phenomenon, float airTemperature, float windSpeed) {
        this.timestamp = timestamp;
        this.location = location;
        this.wmocode = wmocode;
        this.phenomenon = phenomenon;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getWmocode() {
        return wmocode;
    }

    public void setWmocode(Integer wmocode) {
        this.wmocode = wmocode;
    }

    public float getAirTemperature() {
        return airTemperature;
    }

    public void setAirTemperature(Float airTemperature) {
        this.airTemperature = airTemperature;
    }

    public float getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Float windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getPhenomenon() {
        return phenomenon;
    }

    public void setPhenomenon(String phenomenon) {
        this.phenomenon = phenomenon;
    }

    public long getTimestamp() {
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
     * @return true if this object is the same as the argument object, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeatherData data = (WeatherData) o;
        return Objects.equals(timestamp, data.timestamp)
                && Objects.equals(location, data.location)
                && Objects.equals(wmocode, data.wmocode)
                && Objects.equals(phenomenon, data.phenomenon)
                && Objects.equals(airTemperature, data.airTemperature)
                && Objects.equals(windSpeed, data.windSpeed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, location, wmocode, phenomenon, airTemperature, windSpeed);
    }

    @Override
    public String toString() {
        String locationFormat = location == null ? null : "'" + location + "'";
        String phenomenonFormat = phenomenon == null ? null : "'" + phenomenon + "'";

        return "WeatherData{" +
                "id=" + id +
                ", location=" + locationFormat +
                ", wmocode=" + wmocode +
                ", airTemperature=" + airTemperature +
                ", windSpeed=" + windSpeed +
                ", phenomenon=" + phenomenonFormat +
                ", timestamp=" + timestamp +
                '}';
    }
}
