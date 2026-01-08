package org.ikigaidigital.infrastructure.adapter.output.persistence;

import org.ikigaidigital.infrastructure.adapter.output.persistence.entity.TimeDepositEntity;
import org.ikigaidigital.domain.port.output.TimeDepositRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementing TimeDepositRepositoryPort using JPA repository.
 * Bridges the domain layer with the JPA infrastructure.
 */
@Repository
public class TimeDepositRepositoryAdapter implements TimeDepositRepositoryPort {

    private final JpaTimeDepositRepository jpaRepository;

    public TimeDepositRepositoryAdapter(JpaTimeDepositRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<TimeDepositEntity> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<TimeDepositEntity> findAllWithWithdrawals() {
        return jpaRepository.findAllWithWithdrawals();
    }

    @Override
    public Optional<TimeDepositEntity> findById(Integer id) {
        return jpaRepository.findById(id);
    }

    @Override
    public TimeDepositEntity save(TimeDepositEntity entity) {
        return jpaRepository.save(entity);
    }

    @Override
    public List<TimeDepositEntity> saveAll(List<TimeDepositEntity> entities) {
        return jpaRepository.saveAll(entities);
    }
}

