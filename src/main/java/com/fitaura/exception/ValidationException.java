package com.fitaura.exception;

/**
 * Thrown when domain or form input fails application validation criteria.
 */
public class ValidationException extends FitAuraException {

    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
