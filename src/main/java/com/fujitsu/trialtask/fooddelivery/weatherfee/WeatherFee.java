package com.fujitsu.trialtask.fooddelivery.weatherfee;


import com.fujitsu.trialtask.fooddelivery.enums.Vehicle;
import com.fujitsu.trialtask.fooddelivery.enums.WeatherCondition;
import com.fujitsu.trialtask.fooddelivery.helpers.EnumConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;


/**
 * Entity class representing the fee structure based on weather conditions.
 * The fee structure is defined by the vehicle type, weather condition, the fee value
 * and additionally by the weather phenomenon or the weather condition range (e.g. air temperature, wind speed).
 * The fee is applied when the weather data matches the specified conditions.
 * If the fee is null, that means the vehicle is not allowed to operate under the specified conditions.
 */
@Entity
public class WeatherFee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "The vehicle type must be specified")
    @Enumerated(EnumType.STRING)
    private Vehicle vehicle;

    @NotNull(message = "The weather condition must be specified")
    @Enumerated(EnumType.STRING)
    private WeatherCondition condition;

    private Float above;
    private Float below;
    private String phenomenon;

    @Min(value = 0, message = "The fee must be a positive number")
    private Float fee;


    public WeatherFee() {
    }

    public WeatherFee(Vehicle vehicle, WeatherCondition condition, String phenomenon, Float fee) {
        this(vehicle, condition, null, null, phenomenon, fee);
    }

    public WeatherFee(Vehicle vehicle, WeatherCondition condition, Float above, Float below, Float fee) {
        this(vehicle, condition, above, below, null, fee);
    }

    public WeatherFee(Vehicle vehicle, WeatherCondition condition, Float above, Float below, String phenomenon, Float fee) {
        this.vehicle = vehicle;
        this.condition = condition;
        this.below = below;
        this.above = above;
        setPhenomenon(phenomenon);
        this.fee = fee;
    }

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

    public void setVehicle(@org.jetbrains.annotations.NotNull String vehicle) {
        this.vehicle = Vehicle.valueOf(vehicle.toUpperCase());
    }

    public WeatherCondition getCondition() {
        return condition;
    }

    public void setCondition(WeatherCondition condition) {
        this.condition = condition;
    }

    public void setCondition(@org.jetbrains.annotations.NotNull String condition) {
        this.condition = EnumConverter.convertStringToEnum(condition, WeatherCondition.class);
    }

    public Float getAbove() {
        return above;
    }

    public void setAbove(Float above) {
        this.above = above;
    }

    public Float getBelow() {
        return below;
    }

    public void setBelow(Float below) {
        this.below = below;
    }

    public String getPhenomenon() {
        return phenomenon;
    }

    public void setPhenomenon(String phenomenon) {
        this.phenomenon = phenomenon == null ? null : phenomenon.toLowerCase();
    }

    public Float getFee() {
        return fee;
    }

    public void setFee(Float fee) {
        this.fee = fee;
    }

    /**
     * Checks if the provided value falls within the range specified by 'above' and 'below' attributes.
     *
     * @param value the value to check
     *
     * @return true if the value falls within the specified range, false otherwise
     */
    public boolean appliesTo(Float value) {
        if (value == null) {
            return false;
        }

        if (above != null && below != null) {
            return value >= above && value <= below;
        } else if (above != null) {
            return value >= above;
        } else if (below != null) {
            return value <= below;
        }

        return false;
    }

    /**
     * Checks if the provided value contains the phenomenon attribute.
     *
     * @param value the value to check
     *
     * @return true if the value contains the phenomenon, false otherwise
     */
    public boolean appliesTo(String value) {
        return phenomenon != null && value != null && value.contains(phenomenon);
    }

    @Override
    public String toString() {
        return "WeatherFee{" +
                "id=" + id +
                ", vehicle=" + vehicle +
                ", condition=" + condition +
                ", above=" + above +
                ", below=" + below +
                ", phenomenon='" + phenomenon + '\'' +
                ", fee=" + fee +
                '}';
    }

    @AssertTrue(message = "The value of 'above' must be less than or equal to 'below' if both are set")
    private boolean isAboveLessThanBelow() {
        return above == null || below == null || above <= below;
    }

    @AssertFalse(message = "The 'phenomenon' value must be set for that weather condition")
    private boolean hasNoPhenomenonForPhenomenonCondition() {
        return condition == WeatherCondition.PHENOMENON
                && (phenomenon == null || phenomenon.isEmpty());
    }

    @AssertFalse(message = "The 'above' and 'below' values must not be set for that weather condition")
    private boolean hasAboveBelowForPhenomenonCondition() {
        return condition == WeatherCondition.PHENOMENON
                && (above != null || below != null);
    }

    @AssertFalse(message = "The 'phenomenon' value must not be set for that weather condition")
    private boolean hasPhenomenonForNumericalCondition() {
        return (condition == WeatherCondition.AIR_TEMPERATURE || condition == WeatherCondition.WIND_SPEED)
                && phenomenon != null;
    }

    @AssertFalse(message = "Either 'above' or 'below' must be set for that weather condition")
    private boolean hasNoAboveBelowForNumericalCondition() {
        return (condition == WeatherCondition.AIR_TEMPERATURE || condition == WeatherCondition.WIND_SPEED)
                && above == null
                && below == null;
    }
}


