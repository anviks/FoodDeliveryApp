package com.fujitsu.trialtask.fooddelivery.enums;

/**
 * An enum representing the cities where the food can be delivered to and the corresponding weather stations.
 */
public enum City {
    TALLINN("Tallinn-Harku"),
    TARTU("Tartu-Tõravere"),
    PÄRNU("Pärnu");

    private final String station;

    City(String station) {
        this.station = station;
    }

    public String getStation() {
        return station;
    }
}
