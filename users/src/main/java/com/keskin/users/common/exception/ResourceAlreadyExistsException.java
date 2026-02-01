package com.keskin.users.common.exception;

/**
 * Thrown when attempting to create a resource that already exists.
 *
 * <p>Used in the application/service layer to enforce uniqueness rules
 * (e.g. email, username, externalId).</p>
 *
 * <p>Should NOT be thrown from repository implementations.</p>
 *
 * <p>Typically mapped to HTTP {@code 409 Conflict}.</p>
 *
 * <pre>{@code
 * userRepository.findByEmail(email)
 *     .ifPresent(user -> {
 *         throw new ResourceAlreadyExistsException("User", "email", email);
 *     });
 * }</pre>
 */
public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue));
    }
}