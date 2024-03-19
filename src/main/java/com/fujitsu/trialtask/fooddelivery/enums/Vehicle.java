package com.fujitsu.trialtask.fooddelivery.enums;

/**
 * An enum representing the vehicles, that can be used to deliver food. It includes car, scooter and bike.
 */
public enum Vehicle {
    CAR,
    SCOOTER,
    BIKE;

    public static Vehicle getVehicleByName(String vehicleName) {
        for (Vehicle vehicle : Vehicle.values()) {
            if (vehicle.name().equalsIgnoreCase(vehicleName)) {
                return vehicle;
            }
        }
        return null;
    }

    public String toString() {
        return name().toLowerCase();
    }
}

