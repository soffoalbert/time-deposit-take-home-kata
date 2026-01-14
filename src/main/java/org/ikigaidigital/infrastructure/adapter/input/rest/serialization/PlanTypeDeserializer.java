package org.ikigaidigital.infrastructure.adapter.input.rest.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.ikigaidigital.domain.model.PlanType;

import java.io.IOException;

/**
 * Custom Jackson deserializer for PlanType enum.
 * Deserializes lowercase string values from JSON to PlanType enum.
 */
public class PlanTypeDeserializer extends JsonDeserializer<PlanType> {

    @Override
    public PlanType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText();
        if (value == null || value.isEmpty()) {
            return null;
        }
        return PlanType.fromValue(value);
    }
}

