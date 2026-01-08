package org.ikigaidigital.domain.service;

import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.model.TimeDepositCalculator;
import org.ikigaidigital.infrastructure.adapter.output.persistence.entity.TimeDepositEntity;
import org.ikigaidigital.domain.port.input.TimeDepositServicePort;
import org.ikigaidigital.infrastructure.adapter.output.persistence.JpaTimeDepositRepository;
import org.ikigaidigital.infrastructure.adapter.input.rest.dto.TimeDepositResponseDTO;
import org.ikigaidigital.infrastructure.adapter.input.rest.dto.UpdateBalancesResponseDTO;
import org.ikigaidigital.shared.mapper.TimeDepositMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of the TimeDepositServicePort.
 * Contains business logic for time deposit operations.
 */
@Service
public class TimeDepositServiceImpl implements TimeDepositServicePort {

    private final JpaTimeDepositRepository timeDepositRepository;
    private final TimeDepositMapper timeDepositMapper;
    private final TimeDepositCalculator timeDepositCalculator;

    public TimeDepositServiceImpl(
            JpaTimeDepositRepository timeDepositRepository,
            TimeDepositMapper timeDepositMapper,
            TimeDepositCalculator timeDepositCalculator) {
        this.timeDepositRepository = timeDepositRepository;
        this.timeDepositMapper = timeDepositMapper;
        this.timeDepositCalculator = timeDepositCalculator;
    }

    /**
     * Retrieve all time deposits with their associated withdrawals.
     *
     * @return list of time deposit response DTOs
     */
    @Override
    @Transactional(readOnly = true)
    public List<TimeDepositResponseDTO> getAllTimeDeposits() {
        List<TimeDepositEntity> entities = timeDepositRepository.findAllWithWithdrawals();
        return timeDepositMapper.toDTOList(entities);
    }

    /**
     * Update balances for all time deposits by applying interest calculations.
     * Uses the existing TimeDepositCalculator.updateBalance() method.
     *
     * @return response DTO with update status and count
     */
    @Override
    @Transactional
    public UpdateBalancesResponseDTO updateAllBalances() {
        // 1. Fetch all entities
        List<TimeDepositEntity> entities = timeDepositRepository.findAll();

        // 2. Map to domain objects (List<TimeDeposit>)
        List<TimeDeposit> domainObjects = timeDepositMapper.toDomainList(entities);

        // 3. Call existing TimeDepositCalculator.updateBalance()
        timeDepositCalculator.updateBalance(domainObjects);

        // 4. Update entities with new balances
        for (int i = 0; i < entities.size(); i++) {
            timeDepositMapper.updateEntityFromDomain(entities.get(i), domainObjects.get(i));
        }

        // 5. Save all entities
        timeDepositRepository.saveAll(entities);

        // 6. Return response DTO with count
        return new UpdateBalancesResponseDTO(
                "Balances updated successfully",
                entities.size(),
                LocalDateTime.now()
        );
    }
}

