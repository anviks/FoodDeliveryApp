package com.fujitsu.trialtask.fooddelivery.exceptions;

public class ForbiddenVehicleException extends RuntimeException {
    public ForbiddenVehicleException(String message) {
        super(message);
    }

    public ForbiddenVehicleException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForbiddenVehicleException(Throwable cause) {
        super(cause);
    }
}
