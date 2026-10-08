package com.fitaura.exception;

/**
 * Thrown when credentials fail or an unauthenticated user attempts
 * to access a protected resource.
 */
public class AuthenticationException extends FitAuraException {

    private static final long serialVersionUID = 1L;

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
