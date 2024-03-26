package com.fujitsu.trialtask.fooddelivery.weatherfee;

import com.fujitsu.trialtask.fooddelivery.enums.Vehicle;
import com.fujitsu.trialtask.fooddelivery.enums.WeatherCondition;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for handling weather fee-related HTTP requests.
 */
@RestController
@RequestMapping("/api/weather-fees")
public class WeatherFeeController {
    private final WeatherFeeRepository weatherFeeRepository;

    @Autowired
    public WeatherFeeController(WeatherFeeRepository weatherFeeRepository) {
        this.weatherFeeRepository = weatherFeeRepository;
    }

    /**
     * Retrieves all weather fees.
     *
     * @return ResponseEntity containing a list of all weather fees.
     */
    @GetMapping
    public ResponseEntity<List<WeatherFee>> getAllWeatherFees() {
        List<WeatherFee> weatherFees = (List<WeatherFee>) weatherFeeRepository.findAll();
        return ResponseEntity.ok(weatherFees);
    }

    /**
     * Retrieves a weather fee by its ID.
     *
     * @param id The ID of the weather fee to retrieve.
     *
     * @return ResponseEntity containing the requested weather fee.
     * @throws EntityNotFoundException if the requested weather fee does not exist.
     */
    @GetMapping("/{id}")
    public ResponseEntity<WeatherFee> getWeatherFeeById(@PathVariable Long id) {
        WeatherFee weatherFee = weatherFeeRepository.findById(id)
                .orElse(null);
        if (weatherFee == null) {
            throw new EntityNotFoundException("Weather fee not found with id: " + id);
        }
        return ResponseEntity.ok(weatherFee);
    }

    /**
     * Creates a new weather fee.
     *
     * @param weatherFee The weather fee object to create.
     *
     * @return ResponseEntity containing the created weather fee.
     * @throws EntityExistsException if a weather fee with the same vehicle and phenomenon already exists.
     */
    @PostMapping
    public ResponseEntity<WeatherFee> createWeatherFee(@RequestBody WeatherFee weatherFee) {
        validateWeatherFee(weatherFee);
        WeatherFee savedFee = weatherFeeRepository.save(weatherFee);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedFee);
    }

    /**
     * Updates an existing weather fee.
     *
     * @param id                The ID of the weather fee to update.
     * @param updatedWeatherFee The updated weather fee object.
     *
     * @return ResponseEntity containing the updated weather fee.
     * @throws EntityNotFoundException if the weather fee with the specified ID does not exist.
     */
    @PutMapping("/{id}")
    public ResponseEntity<WeatherFee> updateWeatherFee(@PathVariable Long id, @RequestBody WeatherFee updatedWeatherFee) {
        WeatherFee existingWeatherFee = weatherFeeRepository.findById(id)
                .orElse(null);
        if (existingWeatherFee == null) {
            throw new EntityNotFoundException("Weather fee not found with id: " + id);
        }
        validateWeatherFee(updatedWeatherFee);
        updatedWeatherFee.setId(id);
        WeatherFee savedFee = weatherFeeRepository.save(updatedWeatherFee);
        return ResponseEntity.ok(savedFee);
    }

    /**
     * Partially updates an existing weather fee.
     *
     * @param id                The ID of the weather fee to patch.
     * @param partialWeatherFee The partial weather fee object containing the fields to update.
     *
     * @return ResponseEntity containing the patched weather fee.
     * @throws EntityNotFoundException if the weather fee with the specified ID does not exist.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<WeatherFee> patchWeatherFee(@PathVariable Long id, @RequestBody WeatherFee partialWeatherFee) {
        WeatherFee existingWeatherFee = weatherFeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Weather fee not found with id: " + id));

        // Update only the provided fields
        if (partialWeatherFee.getVehicle() != null) {
            existingWeatherFee.setVehicle(partialWeatherFee.getVehicle());
        }
        if (partialWeatherFee.getCondition() != null) {
            existingWeatherFee.setCondition(partialWeatherFee.getCondition());
        }
        if (partialWeatherFee.getAbove() != null) {
            existingWeatherFee.setAbove(partialWeatherFee.getAbove());
        }
        if (partialWeatherFee.getBelow() != null) {
            existingWeatherFee.setBelow(partialWeatherFee.getBelow());
        }
        if (partialWeatherFee.getPhenomenon() != null) {
            existingWeatherFee.setPhenomenon(partialWeatherFee.getPhenomenon());
        }
        if (partialWeatherFee.getFee() != null) {
            existingWeatherFee.setFee(partialWeatherFee.getFee());
        }

        validateWeatherFee(existingWeatherFee);

        WeatherFee patchedFee = weatherFeeRepository.save(existingWeatherFee);
        return ResponseEntity.ok(patchedFee);
    }

    /**
     * Deletes an existing weather fee.
     *
     * @param id The ID of the weather fee to delete.
     *
     * @return ResponseEntity indicating the success of the deletion operation.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWeatherFee(@PathVariable Long id) {
        weatherFeeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private void validateWeatherFee(WeatherFee weatherFee) {
        Vehicle vehicle = weatherFee.getVehicle();
        WeatherCondition condition = weatherFee.getCondition();
        String phenomenon = weatherFee.getPhenomenon();
        Float above = weatherFee.getAbove();
        Float below = weatherFee.getBelow();
        Float fee = weatherFee.getFee();

        if (weatherFeeRepository.existsByVehicleAndConditionAndPhenomenonAndPhenomenonIsNotNull(vehicle, condition, phenomenon)) {
            throw new EntityExistsException("Fee already exists for vehicle: %s and phenomenon: %s".formatted(vehicle, phenomenon));
        }

        Long lessStrictPhenomenon = weatherFeeRepository.findLessStrictHigherPhenomenonFee(vehicle, condition, phenomenon, fee);
        if (lessStrictPhenomenon != null) {
            throw new EntityExistsException("An equal or higher fee already exists for a less strict phenomenon condition (id: %d)".formatted(lessStrictPhenomenon));
        }

        Long overlappingRange = weatherFeeRepository.findOverlappingRange(vehicle, condition, above, below);
        if (overlappingRange != null) {
            throw new EntityExistsException("A fee with an overlapping range exists (id: %d)".formatted(overlappingRange));
        }
    }
}

