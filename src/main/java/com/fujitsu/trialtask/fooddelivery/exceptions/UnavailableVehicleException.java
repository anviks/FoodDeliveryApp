package com.fujitsu.trialtask.fooddelivery.exceptions;

public class UnavailableVehicleException extends RuntimeException {
    public UnavailableVehicleException(String message) {
        super(message);
    }

    public UnavailableVehicleException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnavailableVehicleException(Throwable cause) {
        super(cause);
    }
}
