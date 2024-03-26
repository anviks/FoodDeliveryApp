package com.fujitsu.trialtask.fooddelivery.weatherfee;

import com.fujitsu.trialtask.fooddelivery.enums.Vehicle;
import com.fujitsu.trialtask.fooddelivery.enums.WeatherCondition;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


/**
 * Repository interface for managing weather fees.
 */
public interface WeatherFeeRepository extends CrudRepository<WeatherFee, Long> {

    /**
     * Retrieves all weather fees for a specific vehicle.
     *
     * @param vehicle The vehicle type.
     *
     * @return A list of weather fees for the specified vehicle.
     */
    List<WeatherFee> findAllByVehicle(@NotNull(message = "The vehicle type must be specified") Vehicle vehicle);

    /**
     * Checks if a weather fee exists for the specified vehicle, weather condition, and phenomenon.
     *
     * @param vehicle    The vehicle type.
     * @param condition  The weather condition.
     * @param phenomenon The phenomenon (optional).
     *
     * @return True if a weather fee exists for the specified parameters, false otherwise.
     */
    boolean existsByVehicleAndConditionAndPhenomenonAndPhenomenonIsNotNull(
            @NotNull(message = "The vehicle type must be specified")
            Vehicle vehicle,

            @NotNull(message = "The weather condition must be specified")
            WeatherCondition condition,

            String phenomenon
    );

    /**
     * Finds a weather fee with a less strict phenomenon constraint and a higher fee for the specified vehicle,
     * weather condition, phenomenon, and fee. This is used to prevent a situation where for example a fee for
     * "rain" is set to 10, and a fee for "heavy rain" is about to be set to 5. In this case, the fee for "rain"
     * will always shadow the fee for "heavy rain".
     *
     * @param vehicle    The vehicle type.
     * @param condition  The weather condition.
     * @param phenomenon The phenomenon.
     * @param fee        The fee.
     *
     * @return The ID of a less strict weather fee with a higher phenomenon, or null if not found.
     */
    @Query("""
            SELECT wf.id
            FROM WeatherFee wf
            WHERE wf.vehicle = :vehicle
              AND wf.condition = :condition
              AND wf.phenomenon IS NOT NULL
              AND :phenomenon LIKE "%" || wf.phenomenon || "%"
              AND wf.fee >= :fee
            """)
    Long findLessStrictHigherPhenomenonFee(
            @NotNull(message = "The vehicle type must be specified")
            @Param("vehicle")
            Vehicle vehicle,

            @NotNull(message = "The weather condition must be specified")
            @Param("condition")
            WeatherCondition condition,

            @NotNull(message = "The phenomenon must be specified")
            @Param("phenomenon")
            String phenomenon,

            @Param("fee")
            @Min(value = 0, message = "The fee must be a positive number")
            Float fee
    );

    /**
     * Finds a weather fee with an overlapping range for the specified vehicle, weather condition, above, and below values.
     *
     * @param vehicle   The vehicle type.
     * @param condition The weather condition.
     * @param above     The above value.
     * @param below     The below value.
     *
     * @return The ID of a weather fee with an overlapping range, or null if not found.
     */
    @Query("""
            SELECT wf.id
            FROM WeatherFee wf
            WHERE wf.vehicle = :vehicle
              AND wf.condition = :condition
              AND NOT (wf.below IS NULL
                AND wf.above IS NULL)
              AND (wf.above IS NULL
                OR :below IS NULL
                OR wf.above < :below)
              AND (wf.below IS NULL
                OR :above IS NULL
                OR wf.below > :above)
            """)
    Long findOverlappingRange(
            @NotNull(message = "The vehicle type must be specified")
            @Param("vehicle")
            Vehicle vehicle,

            @NotNull(message = "The weather condition must be specified")
            @Param("condition")
            WeatherCondition condition,

            @Param("above")
            Float above,

            @Param("below")
            Float below
    );
}
