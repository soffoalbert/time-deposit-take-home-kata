package org.ikigaidigital.infrastructure.adapter.input.rest.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.ikigaidigital.domain.model.PlanType;

import java.io.IOException;

/**
 * Custom Jackson serializer for PlanType enum.
 * Serializes the enum as its lowercase string value for JSON responses.
 */
public class PlanTypeSerializer extends JsonSerializer<PlanType> {

    @Override
    public void serialize(PlanType value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            gen.writeString(value.getValue());
        }
    }
}

