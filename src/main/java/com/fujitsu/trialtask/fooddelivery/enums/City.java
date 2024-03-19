package com.fujitsu.trialtask.fooddelivery.enums;

import org.jetbrains.annotations.Nullable;

/**
 * An enum representing the cities where the food can be delivered to.
 * It includes the cities Tallinn, Tartu, and Pärnu.
 */
public enum City {
    TALLINN,
    TARTU,
    PÄRNU;

    /**
     * Returns the city with the specified name or null if the city is not found.
     *
     * @param cityName the name of the city
     * @return the city with the specified name or null if the city is not found
     */
    public static @Nullable City getCityByName(String cityName) {
        for (City city : City.values()) {
            if (city.name().equalsIgnoreCase(cityName)) {
                return city;
            }
        }
        return null;
    }

    public String toString() {
        return name().toLowerCase();
    }
}
