package com.fujitsu.trialtask.fooddelivery.entities;

import com.fujitsu.trialtask.fooddelivery.enums.City;
import com.fujitsu.trialtask.fooddelivery.enums.Vehicle;
import jakarta.persistence.*;

@Entity
public class RegionalFee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private City city;

    @Enumerated(EnumType.STRING)
    private Vehicle vehicle;

    private Float regionalFee;


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

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Float getRegionalFee() {
        return regionalFee;
    }

    public void setRegionalFee(Float baseFee) {
        this.regionalFee = baseFee;
    }
}
