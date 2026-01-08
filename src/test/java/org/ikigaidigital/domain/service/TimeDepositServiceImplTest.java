package org.ikigaidigital.domain.service;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.TimeDepositCalculator;
import org.ikigaidigital.infrastructure.adapter.output.persistence.entity.TimeDepositEntity;
import org.ikigaidigital.infrastructure.adapter.output.persistence.JpaTimeDepositRepository;
import org.ikigaidigital.infrastructure.adapter.input.rest.dto.TimeDepositResponseDTO;
import org.ikigaidigital.infrastructure.adapter.input.rest.dto.UpdateBalancesResponseDTO;
import org.ikigaidigital.shared.mapper.TimeDepositMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TimeDepositServiceImpl Unit Tests")
class TimeDepositServiceImplTest {

    @Mock
    private JpaTimeDepositRepository timeDepositRepository;

    @Mock
    private TimeDepositMapper timeDepositMapper;

    @Mock
    private TimeDepositCalculator timeDepositCalculator;

    @Captor
    private ArgumentCaptor<List<TimeDeposit>> domainListCaptor;

    private TimeDepositServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new TimeDepositServiceImpl(
                timeDepositRepository,
                timeDepositMapper,
                timeDepositCalculator
        );
    }

    @Test
    @DisplayName("getAllTimeDeposits returns correctly mapped DTOs")
    void getAllTimeDeposits_returnsMappedDTOs() {
        // Given
        TimeDepositEntity entity = createEntity(1, "basic", 10000.0, 45);
        List<TimeDepositEntity> entities = List.of(entity);
        TimeDepositResponseDTO expectedDto = new TimeDepositResponseDTO(
                1, "basic", BigDecimal.valueOf(10000.0), 45, Collections.emptyList()
        );

        when(timeDepositRepository.findAllWithWithdrawals()).thenReturn(entities);
        when(timeDepositMapper.toDTOList(entities)).thenReturn(List.of(expectedDto));

        // When
        List<TimeDepositResponseDTO> result = service.getAllTimeDeposits();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1);
        assertThat(result.get(0).planType()).isEqualTo("basic");
        verify(timeDepositRepository).findAllWithWithdrawals();
        verify(timeDepositMapper).toDTOList(entities);
    }

    @Test
    @DisplayName("updateAllBalances calls calculator and saves entities")
    void updateAllBalances_callsCalculatorAndSavesEntities() {
        // Given
        TimeDepositEntity entity = createEntity(1, "basic", 10000.0, 45);
        List<TimeDepositEntity> entities = new ArrayList<>(List.of(entity));
        TimeDeposit domain = new TimeDeposit(1, "basic", 10000.0, 45);
        List<TimeDeposit> domainList = new ArrayList<>(List.of(domain));

        when(timeDepositRepository.findAll()).thenReturn(entities);
        when(timeDepositMapper.toDomainList(entities)).thenReturn(domainList);
        when(timeDepositRepository.saveAll(entities)).thenReturn(entities);

        // When
        UpdateBalancesResponseDTO result = service.updateAllBalances();

        // Then
        assertThat(result.message()).isEqualTo("Balances updated successfully");
        assertThat(result.updatedCount()).isEqualTo(1);
        assertThat(result.timestamp()).isNotNull();

        verify(timeDepositRepository).findAll();
        verify(timeDepositMapper).toDomainList(entities);
        verify(timeDepositCalculator).updateBalance(domainList);
        verify(timeDepositMapper).updateEntityFromDomain(entity, domain);
        verify(timeDepositRepository).saveAll(entities);
    }

    @Test
    @DisplayName("updateAllBalances returns zero count for empty list")
    void updateAllBalances_returnsZeroCountForEmptyList() {
        // Given
        when(timeDepositRepository.findAll()).thenReturn(Collections.emptyList());
        when(timeDepositMapper.toDomainList(anyList())).thenReturn(Collections.emptyList());
        when(timeDepositRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

        // When
        UpdateBalancesResponseDTO result = service.updateAllBalances();

        // Then
        assertThat(result.updatedCount()).isEqualTo(0);
        verify(timeDepositCalculator).updateBalance(Collections.emptyList());
    }

    private TimeDepositEntity createEntity(int id, String planType, double balance, int days) {
        TimeDepositEntity entity = new TimeDepositEntity();
        entity.setId(id);
        entity.setPlanType(planType);
        entity.setBalance(BigDecimal.valueOf(balance));
        entity.setDays(days);
        entity.setWithdrawals(new ArrayList<>());
        return entity;
    }
}

