package com.fitaura.exception;

/**
 * Thrown when persistence or database interaction fails.
 * Wraps low-level SQLExceptions to avoid exposing internal details to higher layers.
 */
public class DatabaseException extends FitAuraException {

    private static final long serialVersionUID = 1L;

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
