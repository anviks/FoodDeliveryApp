package com.fujitsu.trialtask.fooddelivery.entities;

import com.fujitsu.trialtask.fooddelivery.enums.City;
import jakarta.persistence.*;

@Entity
public class CityRegionalFee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "city_id")
    @Enumerated(EnumType.STRING)
    private City city;

    private Float regionalFee;

    // Getters and setters (omitted for brevity)
}

