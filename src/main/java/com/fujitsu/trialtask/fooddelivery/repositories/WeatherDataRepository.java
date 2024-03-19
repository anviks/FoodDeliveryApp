package com.fujitsu.trialtask.fooddelivery.repositories;

import com.fujitsu.trialtask.fooddelivery.entities.WeatherData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * This interface extends Spring Data's {@link org.springframework.data.repository.CrudRepository}
 * and provides additional methods to interact with the {@link WeatherData} entity.
 */
@Repository
public interface WeatherDataRepository extends CrudRepository<WeatherData, Long> {

    /**
     * Returns the most recent {@link WeatherData} object for a given location.
     *
     * @param location the location to search for; the search is case-insensitive and can match partial strings
     * @return the most recent {@link WeatherData} object for the given location, or null if no matching objects are found
     */
    WeatherData findFirstByLocationContainingIgnoreCaseOrderByTimestampDesc(String location);
}
