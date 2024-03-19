package com.fujitsu.trialtask.fooddelivery.entities;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;

import java.util.List;

@Embeddable
public class WeatherPhenomenonCondition {
    private List<String> contains;
    private Float fee;

    @ElementCollection
    public List<String> getContains() {
        return contains;
    }

    public void setContains(List<String> contains) {
        this.contains = contains;
    }

    public Float getFee() {
        return fee;
    }

    public void setFee(Float fee) {
        this.fee = fee;
    }
}
