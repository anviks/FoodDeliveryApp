package com.fujitsu.trialtask.fooddelivery.regionalfee;

import com.fujitsu.trialtask.fooddelivery.enums.City;
import com.fujitsu.trialtask.fooddelivery.enums.Vehicle;
import com.fujitsu.trialtask.fooddelivery.helpers.EnumConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Entity class representing regional fee information for a specific city and vehicle type.
 */
@Entity
public class RegionalFee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "The city must be specified")
    @Enumerated(EnumType.STRING)
    private City city;

    @NotNull(message = "The vehicle type must be specified")
    @Enumerated(EnumType.STRING)
    private Vehicle vehicle;

    @NotNull(message = "The fee must be specified")
    @Min(value = 0, message = "The fee must be a positive number")
    private Float fee;

    /**
     * Default constructor.
     */
    public RegionalFee() {
    }

    /**
     * Constructs a new RegionalFee instance with the specified city, vehicle, and fee.
     *
     * @param city    the city for which the fee is applicable
     * @param vehicle the type of vehicle for which the fee is applicable
     * @param fee     the fee value
     */
    public RegionalFee(City city, Vehicle vehicle, Float fee) {
        this.city = city;
        this.vehicle = vehicle;
        this.fee = fee;
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

    public void setCity(City city) {
        this.city = city;
    }

    public void setCity(@org.jetbrains.annotations.NotNull String city) {
        this.city = EnumConverter.convertStringToEnum(city, City.class);
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public void setVehicle(@org.jetbrains.annotations.NotNull String vehicle) {
        this.vehicle = EnumConverter.convertStringToEnum(vehicle, Vehicle.class);
    }

    public Float getFee() {
        return fee;
    }

    public void setFee(Float baseFee) {
        this.fee = baseFee;
    }

    @Override
    public String toString() {
        return "RegionalFee{" +
                "id=" + id +
                ", city=" + city +
                ", vehicle=" + vehicle +
                ", fee=" + fee +
                '}';
    }
}
