package com.keskin.common.exception;

/**
 * Thrown when a requested resource cannot be found.
 *
 * <p>Intended to be used in the application/service layer after
 * repository calls that return {@code Optional}.</p>
 *
 * <p>Should NOT be thrown from repository implementations.</p>
 *
 * <p>Typically mapped to HTTP {@code 404 Not Found}.</p>
 *
 * <pre>{@code
 * userRepository.findByEmail(email)
 *     .orElseThrow(() ->
 *         new ResourceNotFoundException("User", "email", email)
 *     );
 * }</pre>
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
    }
}