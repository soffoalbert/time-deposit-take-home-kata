package org.ikigaidigital.infrastructure.adapter.output.persistence;

import org.ikigaidigital.infrastructure.adapter.output.persistence.entity.WithdrawalEntity;
import org.ikigaidigital.domain.port.output.WithdrawalRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementing WithdrawalRepositoryPort using JPA repository.
 * Bridges the domain layer with the JPA infrastructure.
 */
@Repository
public class WithdrawalRepositoryAdapter implements WithdrawalRepositoryPort {

    private final JpaWithdrawalRepository jpaRepository;

    public WithdrawalRepositoryAdapter(JpaWithdrawalRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<WithdrawalEntity> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Optional<WithdrawalEntity> findById(Integer id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<WithdrawalEntity> findByTimeDepositId(Integer timeDepositId) {
        return jpaRepository.findByTimeDepositId(timeDepositId);
    }

    @Override
    public WithdrawalEntity save(WithdrawalEntity entity) {
        return jpaRepository.save(entity);
    }
}

