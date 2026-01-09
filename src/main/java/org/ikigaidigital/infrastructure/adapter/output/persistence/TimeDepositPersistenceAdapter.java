package org.ikigaidigital.infrastructure.adapter.output.persistence;

import org.ikigaidigital.application.port.output.TimeDepositPersistencePort;
import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.infrastructure.adapter.output.persistence.entity.TimeDepositEntity;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Persistence adapter implementing TimeDepositPersistencePort.
 * 
 * This adapter handles the conversion between domain objects and JPA entities,
 * completely encapsulating persistence concerns within the infrastructure layer.
 */
@Repository
public class TimeDepositPersistenceAdapter implements TimeDepositPersistencePort {

    private final JpaTimeDepositRepository jpaRepository;

    public TimeDepositPersistenceAdapter(JpaTimeDepositRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<TimeDeposit> findAllWithWithdrawals() {
        return jpaRepository.findAllWithWithdrawals().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<TimeDeposit> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<TimeDeposit> saveAll(List<TimeDeposit> timeDeposits) {
        // First, fetch existing entities to update them
        List<TimeDepositEntity> existingEntities = jpaRepository.findAll();

        // Track entities that actually need updating
        List<TimeDepositEntity> entitiesToUpdate = new ArrayList<>();

        // Update entities with new balances from domain objects, only if balance changed
        for (TimeDeposit domain : timeDeposits) {
            existingEntities.stream()
                    .filter(e -> e.getId().equals(domain.getId()))
                    .findFirst()
                    .ifPresent(entity -> {
                        BigDecimal newBalance = BigDecimal.valueOf(domain.getBalance());
                        // Only update if balance actually changed
                        if (entity.getBalance().compareTo(newBalance) != 0) {
                            entity.setBalance(newBalance);
                            entitiesToUpdate.add(entity);
                        }
                    });
        }

        // Save only entities that changed
        if (!entitiesToUpdate.isEmpty()) {
            jpaRepository.saveAll(entitiesToUpdate);
        }

        // Return all domain objects (including unchanged ones)
        return timeDeposits;
    }

    /**
     * Convert a TimeDepositEntity to a TimeDeposit domain object.
     */
    private TimeDeposit toDomain(TimeDepositEntity entity) {
        return new TimeDeposit(
                entity.getId(),
                entity.getPlanType(),
                entity.getBalance().doubleValue(),
                entity.getDays()
        );
    }
}

