package com.nova.bank.novabank.exception;

public class CustomerValidationException extends RuntimeException {
    private String details;

    public CustomerValidationException(String message) {
        super(message);
    }

    public CustomerValidationException(String message, String details) {
        super(message);
        this.details = details;
    }

    public String getDetails() {
        return details;
    }
}