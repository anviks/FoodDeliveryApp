package com.fujitsu.trialtask.fooddelivery.controllers;

import com.fujitsu.trialtask.fooddelivery.entities.RegionalFee;
import com.fujitsu.trialtask.fooddelivery.repositories.RegionalFeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regional-fees")
public class RegionalFeeController {
    private final RegionalFeeRepository regionalFeeRepository;

    @Autowired
    public RegionalFeeController(RegionalFeeRepository regionalFeeRepository) {
        this.regionalFeeRepository = regionalFeeRepository;
    }

    @GetMapping
    public ResponseEntity<List<RegionalFee>> getAllRegionalFees() {
        List<RegionalFee> regionalFees = (List<RegionalFee>) regionalFeeRepository.findAll();
        return ResponseEntity.ok(regionalFees);
    }

    @PostMapping
    public ResponseEntity<RegionalFee> createRegionalFee(@RequestBody RegionalFee regionalFee) {
        RegionalFee savedFee = regionalFeeRepository.save(regionalFee);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedFee);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegionalFee> getRegionalFeeById(@PathVariable Long id) {
        RegionalFee regionalFee = regionalFeeRepository.findById(id)
                .orElse(null);
        if (regionalFee == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(regionalFee);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegionalFee> updateRegionalFee(@PathVariable Long id, @RequestBody RegionalFee updatedRegionalFee) {
        RegionalFee existingRegionalFee = regionalFeeRepository.findById(id)
                .orElse(null);
        if (existingRegionalFee == null) {
            return ResponseEntity.notFound().build();
        }
        updatedRegionalFee.setId(id);
        RegionalFee savedFee = regionalFeeRepository.save(updatedRegionalFee);
        return ResponseEntity.ok(savedFee);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegionalFee(@PathVariable Long id) {
        regionalFeeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
