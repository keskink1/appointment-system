package com.keskin.common.exception;

import com.keskin.common.dto.response.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Centralized exception handling across all microservices.
 * Intercepts business and system exceptions to return standardized error responses.
 * Hidden to prevent swagger exception
 */
@Hidden
@RestControllerAdvice(basePackages = {"com.keskin.users", "com.keskin.common","com.keskin.appointments", "com.keskin.notifications", "com.keskin.gatewayserver"})
public class GlobalExceptionHandler {

    /**
     * Handles missing resource scenarios.
     * @return 404 Not Found
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Handles duplicate record attempts, typically for email or unique constraints.
     * @return 409 Conflict
     */
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * Handles validation failures related to empty fields or invalid data formats.
     * @return 400 Bad Request
     */
    @ExceptionHandler({NoEmptyFieldException.class, InvalidValidationException.class})
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(RuntimeException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Catches any unhandled exceptions to prevent leaking raw system details.
     * @return 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneralException(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + ex.getMessage());
    }


    /**
     * Handles authentication failures, such as incorrect credentials.
     * @return 401 Unauthorized
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidLoginCredentials() {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid email or password");
    }

    /**
     * Handles authorization failures when a user lacks the necessary roles or permissions.
     * @return 403 Forbidden
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponseDto> handleForbidden(UnauthorizedException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN , "You are not authorized! " + ex.getMessage());
    }

    /**
     * Helper method to construct the standard error response body.
     */
    private ResponseEntity<ErrorResponseDto> buildErrorResponse(HttpStatus status, String message) {
        ErrorResponseDto error = new ErrorResponseDto(
                status.value(),
                message,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, status);
    }
}