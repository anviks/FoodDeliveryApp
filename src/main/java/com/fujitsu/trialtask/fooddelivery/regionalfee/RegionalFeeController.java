package com.fujitsu.trialtask.fooddelivery.regionalfee;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * Controller class for handling CRUD operations related to regional fees.
 */
@RestController
@RequestMapping("/api/regional-fees")
class RegionalFeeController {
    private final RegionalFeeRepository regionalFeeRepository;

    @Autowired
    public RegionalFeeController(RegionalFeeRepository regionalFeeRepository) {
        this.regionalFeeRepository = regionalFeeRepository;
    }

    /**
     * Retrieves all regional fees.
     *
     * @return ResponseEntity containing the list of regional fees
     */
    @GetMapping
    public ResponseEntity<Iterable<RegionalFee>> getAllRegionalFees() {
        Iterable<RegionalFee> regionalFees = regionalFeeRepository.findAll();
        return ResponseEntity.ok(regionalFees);
    }

    /**
     * Retrieves a regional fee by its ID.
     *
     * @param id the ID of the regional fee to retrieve
     *
     * @return ResponseEntity containing the regional fee if found, otherwise 404 Not Found
     * @throws EntityNotFoundException if the regional fee with the specified ID is not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<RegionalFee> getRegionalFeeById(@PathVariable Long id) {
        RegionalFee regionalFee = regionalFeeRepository.findById(id).orElse(null);
        if (regionalFee == null) {
            throw new EntityNotFoundException("Regional fee not found with id: " + id);
        }
        return ResponseEntity.ok(regionalFee);
    }

    /**
     * Creates a new regional fee.
     *
     * @param regionalFee the regional fee to create
     *
     * @return ResponseEntity containing the created regional fee and HTTP status 201 Created
     * @throws EntityExistsException if a regional fee already exists for the specified city and vehicle
     */
    @PostMapping
    public ResponseEntity<RegionalFee> createRegionalFee(@RequestBody RegionalFee regionalFee) {
        if (regionalFeeRepository.existsByCityAndVehicle(regionalFee.getCity(), regionalFee.getVehicle())) {
            throw new EntityExistsException("Regional fee already exists for city: " + regionalFee.getCity() + " and vehicle: " + regionalFee.getVehicle());
        }
        RegionalFee savedFee = regionalFeeRepository.save(regionalFee);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedFee);
    }

    /**
     * Updates an existing regional fee.
     *
     * @param id                 the ID of the regional fee to update
     * @param updatedRegionalFee the updated regional fee data
     *
     * @return ResponseEntity containing the updated regional fee and HTTP status 200 OK
     * @throws EntityNotFoundException if no regional fee is found with the specified ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<RegionalFee> updateRegionalFee(@PathVariable Long id, @RequestBody RegionalFee updatedRegionalFee) {
        RegionalFee existingRegionalFee = regionalFeeRepository.findById(id)
                .orElse(null);
        if (existingRegionalFee == null) {
            throw new EntityNotFoundException("Regional fee not found with id: " + id);
        }
        updatedRegionalFee.setId(id);
        RegionalFee savedFee = regionalFeeRepository.save(updatedRegionalFee);
        return ResponseEntity.ok(savedFee);
    }

    /**
     * Partially updates an existing regional fee.
     *
     * @param id                 the ID of the regional fee to update
     * @param updatedRegionalFee the partially updated regional fee data
     *
     * @return ResponseEntity containing the updated regional fee and HTTP status 200 OK
     * @throws EntityNotFoundException if no regional fee is found with the specified ID
     */
    @PatchMapping("/{id}")
    public ResponseEntity<RegionalFee> patchRegionalFee(@PathVariable Long id, @RequestBody RegionalFee updatedRegionalFee) {
        RegionalFee existingRegionalFee = regionalFeeRepository.findById(id).orElse(null);
        if (existingRegionalFee == null) {
            throw new EntityNotFoundException("Regional fee not found with id: " + id);
        }

        // Update only the fields that are not null
        if (updatedRegionalFee.getCity() != null) {
            existingRegionalFee.setCity(updatedRegionalFee.getCity());
        }
        if (updatedRegionalFee.getVehicle() != null) {
            existingRegionalFee.setVehicle(updatedRegionalFee.getVehicle());
        }
        if (updatedRegionalFee.getFee() != null) {
            existingRegionalFee.setFee(updatedRegionalFee.getFee());
        }

        RegionalFee savedFee = regionalFeeRepository.save(existingRegionalFee);
        return ResponseEntity.ok(savedFee);
    }

    /**
     * Deletes a regional fee by ID.
     *
     * @param id the ID of the regional fee to delete
     *
     * @return ResponseEntity with HTTP status 204 NO CONTENT
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegionalFee(@PathVariable Long id) {
        regionalFeeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
