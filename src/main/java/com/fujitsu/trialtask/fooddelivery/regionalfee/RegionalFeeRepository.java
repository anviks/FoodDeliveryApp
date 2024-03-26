package com.fujitsu.trialtask.fooddelivery.regionalfee;

import com.fujitsu.trialtask.fooddelivery.enums.City;
import com.fujitsu.trialtask.fooddelivery.enums.Vehicle;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing regional fee entities.
 */
@Repository
public interface RegionalFeeRepository extends CrudRepository<RegionalFee, Long> {
    /**
     * Retrieves a regional fee by city and vehicle type.
     *
     * @param city    the city for which the regional fee is being retrieved
     * @param vehicle the vehicle type for which the regional fee is being retrieved
     *
     * @return the regional fee matching the specified city and vehicle type, or null if not found
     */
    RegionalFee findByCityAndVehicle(
            @NotNull(message = "The city must be specified") City city,
            @NotNull(message = "The vehicle type must be specified") Vehicle vehicle);

    /**
     * Checks if a regional fee exists for the specified city and vehicle type.
     *
     * @param city    the city for which to check the existence of a regional fee
     * @param vehicle the vehicle type for which to check the existence of a regional fee
     *
     * @return true if a regional fee exists for the specified city and vehicle type, otherwise false
     */
    boolean existsByCityAndVehicle(
            @NotNull(message = "The city must be specified") City city,
            @NotNull(message = "The vehicle type must be specified") Vehicle vehicle);
}
