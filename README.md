# Food Delivery Application

Welcome to the Food Delivery Application documentation. This API provides endpoints for managing weather-related fees
and
regional fees for food delivery services. It also allows users to calculate delivery fees based on current weather
conditions.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Getting Started](#getting-started)
- [Endpoints](#endpoints)

## Overview

Food Delivery Application is a RESTful API built to facilitate the management of weather fees and regional fees for food
delivery services. It allows users to perform CRUD operations on weather fees and regional fees, as well as calculate
delivery fees based on weather conditions and regional settings.

## Features

- **Weather Fees Management:** CRUD operations for managing weather-related fees based on various conditions such as air
  temperature, wind speed, and phenomenon.
- **Regional Fees Management:** CRUD operations for managing regional fees for different cities and vehicle types.
- **Delivery Fee Calculation:** Endpoint for calculating delivery fees based on weather conditions and regional
  settings.
- **Partial Updates:** Support for partially updating weather fees and regional fees.
- **Error Handling:** Custom error messages and responses for better user experience.

## Getting Started

To get started with the Food Delivery Application, follow these steps:

1. Clone the repository:
    ```bash
    git clone https://github.com/anviks/FoodDeliveryApp.git
    ```
2. Navigate to the project directory:
    ```bash
    cd FoodDeliveryApp
    ```
3. Install the dependencies:
    ```bash
    mvn install
    ```
4. Run the application:
    ```bash
    mvn spring-boot:run
    ```

The application will start running on `http://localhost:8080`.

## Endpoints

The Food Delivery Application provides the following endpoints:

- Weather Fees:
    - `GET /api/weather-fees`: Retrieve all weather fees.
    - `GET /api/weather-fees/{id}`: Retrieve a weather fee by ID.
    - `POST /api/weather-fees`: Create a new weather fee.
    - `PUT /api/weather-fees/{id}`: Update a weather fee by ID.
    - `PATCH /api/weather-fees/{id}`: Partially update a weather fee by ID.
    - `DELETE /api/weather-fees/{id}`: Delete a weather fee by ID.
- Regional Fees:
    - `GET /api/regional-fees`: Retrieve all regional fees.
    - `GET /api/regional-fees/{id}`: Retrieve a regional fee by ID.
    - `POST /api/regional-fees`: Create a new regional fee.
    - `PUT /api/regional-fees/{id}`: Update a regional fee by ID.
    - `PATCH /api/regional-fees/{id}`: Partially update a regional fee by ID.
    - `DELETE /api/regional-fees/{id}`: Delete a regional fee by ID.
- Delivery Fee Calculation:
    - `GET /api/delivery/{city}`: Calculate delivery fee for a specific city and vehicle.

For more details on the request and response formats,
refer to the API documentation [here](src/main/resources/static/food-delivery-api.yaml)
or visit the Swagger UI at [localhost](http://localhost:8080/swagger-ui/index.html) when the application is running.
