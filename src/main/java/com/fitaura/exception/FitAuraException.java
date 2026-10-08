package com.fitaura.exception;

/**
 * Base root runtime exception for the FitAura application.
 * All application-specific exceptions inherit from this class.
 */
public class FitAuraException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public FitAuraException(String message) {
        super(message);
    }

    public FitAuraException(String message, Throwable cause) {
        super(message, cause);
    }
}
