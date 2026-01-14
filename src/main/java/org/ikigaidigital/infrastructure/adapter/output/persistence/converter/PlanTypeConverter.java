package org.ikigaidigital.infrastructure.adapter.output.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.ikigaidigital.domain.model.PlanType;

/**
 * JPA AttributeConverter for converting PlanType enum to String for database persistence.
 * 
 * This converter ensures that PlanType enums are stored as lowercase strings in the database
 * while maintaining type safety in the domain model.
 * 
 * The @Converter(autoApply = true) annotation ensures this converter is automatically
 * applied to all PlanType fields in JPA entities.
 */
@Converter(autoApply = true)
public class PlanTypeConverter implements AttributeConverter<PlanType, String> {

    @Override
    public String convertToDatabaseColumn(PlanType planType) {
        if (planType == null) {
            return null;
        }
        return planType.getValue();
    }

    @Override
    public PlanType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return PlanType.fromValue(dbData);
    }
}

