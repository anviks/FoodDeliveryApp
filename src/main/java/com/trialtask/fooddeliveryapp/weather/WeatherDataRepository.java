package com.trialtask.fooddeliveryapp.weather;

import org.springframework.data.repository.CrudRepository;

/**
 * This interface extends Spring Data's {@link org.springframework.data.repository.CrudRepository}
 * and provides additional methods to interact with the {@link com.trialtask.fooddeliveryapp.weather.WeatherData} entity.
 */
public interface WeatherDataRepository extends CrudRepository<WeatherData, Long> {

    /**
     * Returns the most recent {@link com.trialtask.fooddeliveryapp.weather.WeatherData} object for a given location.
     *
     * @param location the location to search for; the search is case-insensitive and can match partial strings
     * @return the most recent {@link com.trialtask.fooddeliveryapp.weather.WeatherData} object for the given location, or null if no matching objects are found
     */
    WeatherData findFirstByLocationContainingIgnoreCaseOrderByTimestampDesc(String location);
}
