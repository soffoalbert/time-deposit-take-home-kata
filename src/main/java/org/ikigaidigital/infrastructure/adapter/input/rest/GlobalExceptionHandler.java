package org.ikigaidigital.infrastructure.adapter.input.rest;

import org.ikigaidigital.infrastructure.adapter.input.rest.dto.ErrorResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Global exception handler for the REST API.
 * 
 * Security considerations:
 * - Never expose stack traces or internal error details to API clients
 * - Log full error details server-side for debugging
 * - Return consistent, generic error messages to clients
 * - Use appropriate HTTP status codes
 * 
 * Note: Per requirements, detailed exception handling is not required,
 * but this handler prevents information disclosure through stack traces.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle 404 Not Found errors.
     * This prevents Spring's default error handling from exposing details.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(NoResourceFoundException ex) {
        log.warn("Resource not found: {}", ex.getResourcePath());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponseDTO.of("NOT_FOUND", "The requested resource was not found"));
    }

    /**
     * Handle IllegalArgumentException for bad requests.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Invalid argument: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseDTO.of("BAD_REQUEST", "Invalid request parameters"));
    }

    /**
     * Catch-all handler for unexpected exceptions.
     * 
     * Security: Log the full exception server-side but return a generic message.
     * This prevents information disclosure through error messages.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
        // Log full details for debugging - but never send to client
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponseDTO.of("INTERNAL_ERROR", "An unexpected error occurred. Please try again later."));
    }
}

