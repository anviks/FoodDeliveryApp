package com.fujitsu.trialtask.fooddelivery.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;
import java.util.List;


/**
 * Represents an API error response.
 */
public class ApiError {
    @JsonIgnore
    private HttpStatus status;
    private Integer statusCode;
    private String statusDescription;
    private ZonedDateTime timestamp;
    private String message;
    private List<String> errorDetails;


    /**
     * Constructs an {@code ApiError} instance with the specified HTTP status.
     *
     * @param status The HTTP status of the error.
     */
    public ApiError(HttpStatus status) {
        this(status, "Unexpected error");
    }

    /**
     * Constructs an {@code ApiError} instance with the specified HTTP status and message.
     *
     * @param status  The HTTP status of the error.
     * @param message A description of the error.
     */
    public ApiError(HttpStatus status, String message) {
        this(status, message, List.of());
    }

    /**
     * Constructs an {@code ApiError} instance with the specified HTTP status, message, and error details.
     *
     * @param status       The HTTP status of the error.
     * @param message      A description of the error.
     * @param errorDetails Additional details about the error.
     */
    public ApiError(HttpStatus status, String message, List<String> errorDetails) {
        this.setStatus(status);
        this.message = message;
        this.errorDetails = errorDetails;
        this.timestamp = ZonedDateTime.now();
    }


    public HttpStatus getStatus() {
        return status;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
        this.statusCode = status.value();
        this.statusDescription = status.getReasonPhrase();
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(List<String> errorDetails) {
        this.errorDetails = errorDetails;
    }

    public void addErrorDetail(String errorDetail) {
        this.errorDetails.add(errorDetail);
    }
}
