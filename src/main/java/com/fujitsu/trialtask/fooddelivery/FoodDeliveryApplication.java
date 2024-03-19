package com.fujitsu.trialtask.fooddelivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The main class for the Food Delivery Application.
 */
@SpringBootApplication
@EnableScheduling
public class FoodDeliveryApplication {
    /**
     * The main method for the application.
     *
     * @param args the command-line arguments for the application
     */
    public static void main(String[] args) {
        SpringApplication.run(FoodDeliveryApplication.class, args);
    }
}
