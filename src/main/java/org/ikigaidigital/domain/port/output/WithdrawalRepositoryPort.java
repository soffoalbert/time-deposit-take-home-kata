package org.ikigaidigital.domain.port.output;

import org.ikigaidigital.infrastructure.adapter.output.persistence.entity.WithdrawalEntity;

import java.util.List;
import java.util.Optional;

/**
 * Output port interface for Withdrawal persistence operations.
 * Defines the contract for repository operations in the domain layer.
 */
public interface WithdrawalRepositoryPort {

    /**
     * Find all withdrawals.
     *
     * @return list of all withdrawal entities
     */
    List<WithdrawalEntity> findAll();

    /**
     * Find a withdrawal by its ID.
     *
     * @param id the withdrawal ID
     * @return optional containing the withdrawal if found
     */
    Optional<WithdrawalEntity> findById(Integer id);

    /**
     * Find all withdrawals for a specific time deposit.
     *
     * @param timeDepositId the time deposit ID
     * @return list of withdrawals for the time deposit
     */
    List<WithdrawalEntity> findByTimeDepositId(Integer timeDepositId);

    /**
     * Save a withdrawal entity.
     *
     * @param entity the withdrawal to save
     * @return the saved withdrawal entity
     */
    WithdrawalEntity save(WithdrawalEntity entity);
}

