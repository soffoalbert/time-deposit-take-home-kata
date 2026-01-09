package org.ikigaidigital.infrastructure.adapter.input.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Standardized error response DTO for API error responses.
 * 
 * This provides a consistent error format that:
 * - Hides internal implementation details (no stack traces)
 * - Provides meaningful error codes for client handling
 * - Includes timestamp for debugging/logging correlation
 * 
 * Security consideration: Never expose internal error details to clients.
 */
@Schema(description = "Error response returned when an API request fails")
public record ErrorResponseDTO(
        @Schema(description = "Error code for client-side handling", example = "INTERNAL_ERROR")
        String errorCode,

        @Schema(description = "Human-readable error message", example = "An unexpected error occurred")
        String message,

        @Schema(description = "Timestamp when the error occurred", example = "2024-01-15T10:30:00")
        LocalDateTime timestamp
) {
    /**
     * Factory method to create an error response with current timestamp.
     */
    public static ErrorResponseDTO of(String errorCode, String message) {
        return new ErrorResponseDTO(errorCode, message, LocalDateTime.now());
    }
}

