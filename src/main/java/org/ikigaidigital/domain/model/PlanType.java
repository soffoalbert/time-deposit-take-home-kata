package org.ikigaidigital.domain.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.ikigaidigital.infrastructure.adapter.input.rest.serialization.PlanTypeDeserializer;
import org.ikigaidigital.infrastructure.adapter.input.rest.serialization.PlanTypeSerializer;

/**
 * Enum representing the different types of time deposit plans.
 *
 * This enum replaces magic strings throughout the codebase and provides
 * type safety for plan type operations.
 *
 * Plan Types:
 * - BASIC: 1% annual interest rate, 30-day grace period
 * - STUDENT: 3% annual interest rate, 30-day grace period, 366-day cutoff
 * - PREMIUM: 5% annual interest rate, 45-day minimum
 * - INTERNAL: 8.5% annual interest rate, no grace period, terminates at 300 days
 *
 * JSON serialization is handled by custom serializer/deserializer to ensure
 * lowercase string values are used in API responses and requests.
 */
@JsonSerialize(using = PlanTypeSerializer.class)
@JsonDeserialize(using = PlanTypeDeserializer.class)
public enum PlanType {
    BASIC("basic"),
    STUDENT("student"),
    PREMIUM("premium"),
    INTERNAL("internal");

    private final String value;

    PlanType(String value) {
        this.value = value;
    }

    /**
     * Get the string value of the plan type.
     * Used for database persistence and API serialization.
     * 
     * @return the lowercase string representation
     */
    public String getValue() {
        return value;
    }

    /**
     * Convert a string value to a PlanType enum.
     * 
     * @param value the string value to convert (case-insensitive)
     * @return the corresponding PlanType, or null if not found
     */
    public static PlanType fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (PlanType type : PlanType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }

    /**
     * Returns the lowercase string representation of the plan type.
     * 
     * @return the lowercase string value
     */
    @Override
    public String toString() {
        return value;
    }
}

