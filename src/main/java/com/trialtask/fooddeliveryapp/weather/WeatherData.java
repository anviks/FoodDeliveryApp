package com.trialtask.fooddeliveryapp.weather;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Objects;

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

    public WeatherData(long timestamp, String location, int wmocode, String phenomenon, float airTemperature, float windSpeed) {
        this.timestamp = timestamp;
        this.location = location;
        this.wmocode = wmocode;
        this.phenomenon = phenomenon;
        this.airTemperature = airTemperature;
        this.windSpeed = windSpeed;
    }

    public WeatherData() {}

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

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
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
