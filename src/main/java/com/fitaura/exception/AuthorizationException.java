package com.fitaura.exception;

/**
 * Thrown when an authenticated user attempts to perform an operation
 * or access an entity without having the required role or ownership.
 */
public class AuthorizationException extends FitAuraException {

    private static final long serialVersionUID = 1L;

    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
