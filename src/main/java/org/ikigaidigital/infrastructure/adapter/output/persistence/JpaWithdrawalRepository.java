package org.ikigaidigital.infrastructure.adapter.output.persistence;

import org.ikigaidigital.domain.model.WithdrawalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository for WithdrawalEntity.
 * Provides CRUD operations and custom queries for withdrawals.
 */
@Repository
public interface JpaWithdrawalRepository extends JpaRepository<WithdrawalEntity, Integer> {

    /**
     * Find all withdrawals for a specific time deposit.
     *
     * @param timeDepositId the time deposit ID
     * @return list of withdrawals for the time deposit
     */
    List<WithdrawalEntity> findByTimeDepositId(Integer timeDepositId);
}

