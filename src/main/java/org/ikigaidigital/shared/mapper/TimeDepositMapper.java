package org.ikigaidigital.shared.mapper;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.infrastructure.adapter.output.persistence.entity.TimeDepositEntity;
import org.ikigaidigital.infrastructure.adapter.output.persistence.entity.WithdrawalEntity;
import org.ikigaidigital.infrastructure.adapter.input.rest.dto.TimeDepositResponseDTO;
import org.ikigaidigital.infrastructure.adapter.input.rest.dto.WithdrawalDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between TimeDeposit entities, domain objects, and DTOs.
 */
@Component
public class TimeDepositMapper {

    /**
     * Convert a TimeDepositEntity to a TimeDepositResponseDTO.
     *
     * @param entity the entity to convert
     * @return the response DTO
     */
    public TimeDepositResponseDTO toDTO(TimeDepositEntity entity) {
        if (entity == null) {
            return null;
        }

        List<WithdrawalDTO> withdrawalDTOs = entity.getWithdrawals().stream()
                .map(this::toWithdrawalDTO)
                .collect(Collectors.toList());

        return new TimeDepositResponseDTO(
                entity.getId(),
                entity.getPlanType(),
                entity.getBalance(),
                entity.getDays(),
                withdrawalDTOs
        );
    }

    /**
     * Convert a list of TimeDepositEntities to a list of TimeDepositResponseDTOs.
     *
     * @param entities the entities to convert
     * @return the list of response DTOs
     */
    public List<TimeDepositResponseDTO> toDTOList(List<TimeDepositEntity> entities) {
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert a TimeDepositEntity to the existing TimeDeposit domain object.
     *
     * @param entity the entity to convert
     * @return the domain object
     */
    public TimeDeposit toDomain(TimeDepositEntity entity) {
        if (entity == null) {
            return null;
        }

        return new TimeDeposit(
                entity.getId(),
                entity.getPlanType(),
                entity.getBalance().doubleValue(),
                entity.getDays()
        );
    }

    /**
     * Convert a list of TimeDepositEntities to a list of TimeDeposit domain objects.
     *
     * @param entities the entities to convert
     * @return the list of domain objects
     */
    public List<TimeDeposit> toDomainList(List<TimeDepositEntity> entities) {
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Update an entity's balance from a TimeDeposit domain object.
     *
     * @param entity the entity to update
     * @param domain the domain object with the new balance
     */
    public void updateEntityFromDomain(TimeDepositEntity entity, TimeDeposit domain) {
        if (entity != null && domain != null) {
            entity.setBalance(BigDecimal.valueOf(domain.getBalance()));
        }
    }

    /**
     * Convert a WithdrawalEntity to a WithdrawalDTO.
     *
     * @param entity the entity to convert
     * @return the DTO
     */
    private WithdrawalDTO toWithdrawalDTO(WithdrawalEntity entity) {
        if (entity == null) {
            return null;
        }

        return new WithdrawalDTO(
                entity.getId(),
                entity.getAmount(),
                entity.getWithdrawalDate()
        );
    }
}

