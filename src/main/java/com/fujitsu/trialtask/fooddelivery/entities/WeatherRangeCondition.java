package com.fujitsu.trialtask.fooddelivery.entities;

import jakarta.persistence.Embeddable;

@Embeddable
public class WeatherRangeCondition {
    private Integer below;
    private Integer above;
    private Float fee;


    public Integer getBelow() {
        return below;
    }

    public void setBelow(Integer below) {
        this.below = below;
    }

    public Integer getAbove() {
        return above;
    }

    public void setAbove(Integer above) {
        this.above = above;
    }

    public Float getFee() {
        return fee;
    }

    public void setFee(Float fee) {
        this.fee = fee;
    }
}
