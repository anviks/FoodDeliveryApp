package com.fujitsu.trialtask.fooddelivery.weatherdata;

import com.fujitsu.trialtask.fooddelivery.enums.City;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing weather data entities.
 */
@Repository
public interface WeatherDataRepository extends CrudRepository<WeatherData, Long> {

    /**
     * Retrieves the latest weather data for the specified city.
     *
     * @param city the city for which to retrieve the latest weather data
     *
     * @return the latest weather data for the specified city, or null if not found
     */
    WeatherData findFirstByCityOrderByTimestampDesc(City city);
}
