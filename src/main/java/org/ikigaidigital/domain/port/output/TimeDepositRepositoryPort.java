package org.ikigaidigital.domain.port.output;

import org.ikigaidigital.domain.model.TimeDepositEntity;

import java.util.List;
import java.util.Optional;

/**
 * Output port interface for TimeDeposit persistence operations.
 * Defines the contract for repository operations in the domain layer.
 */
public interface TimeDepositRepositoryPort {

    /**
     * Find all time deposits.
     *
     * @return list of all time deposit entities
     */
    List<TimeDepositEntity> findAll();

    /**
     * Find all time deposits with their withdrawals eagerly loaded.
     *
     * @return list of all time deposit entities with withdrawals
     */
    List<TimeDepositEntity> findAllWithWithdrawals();

    /**
     * Find a time deposit by its ID.
     *
     * @param id the time deposit ID
     * @return optional containing the time deposit if found
     */
    Optional<TimeDepositEntity> findById(Integer id);

    /**
     * Save a time deposit entity.
     *
     * @param entity the time deposit to save
     * @return the saved time deposit entity
     */
    TimeDepositEntity save(TimeDepositEntity entity);

    /**
     * Save all time deposit entities.
     *
     * @param entities the time deposits to save
     * @return the saved time deposit entities
     */
    List<TimeDepositEntity> saveAll(List<TimeDepositEntity> entities);
}

