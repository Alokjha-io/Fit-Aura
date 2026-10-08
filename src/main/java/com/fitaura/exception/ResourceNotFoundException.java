package com.fitaura.exception;

/**
 * Thrown when an expected entity or resource cannot be found in the system.
 */
public class ResourceNotFoundException extends FitAuraException {

    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
