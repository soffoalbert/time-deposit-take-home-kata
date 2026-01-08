package org.ikigaidigital.infrastructure.adapter.output.persistence;

import org.ikigaidigital.domain.model.TimeDepositEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository for TimeDepositEntity.
 * Provides CRUD operations and custom queries for time deposits.
 */
@Repository
public interface JpaTimeDepositRepository extends JpaRepository<TimeDepositEntity, Integer> {

    /**
     * Find all time deposits with their withdrawals eagerly loaded.
     * Uses a JOIN FETCH to avoid N+1 query problem.
     *
     * @return list of time deposits with withdrawals loaded
     */
    @Query("SELECT DISTINCT t FROM TimeDepositEntity t LEFT JOIN FETCH t.withdrawals")
    List<TimeDepositEntity> findAllWithWithdrawals();

    /**
     * Find time deposits by plan type.
     *
     * @param planType the plan type to filter by
     * @return list of time deposits matching the plan type
     */
    List<TimeDepositEntity> findByPlanType(String planType);
}

