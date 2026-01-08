package org.ikigaidigital.shared.mapper;

import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.infrastructure.adapter.output.persistence.entity.TimeDepositEntity;
import org.ikigaidigital.infrastructure.adapter.output.persistence.entity.WithdrawalEntity;
import org.ikigaidigital.infrastructure.adapter.input.rest.dto.TimeDepositResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TimeDepositMapper Unit Tests")
class TimeDepositMapperTest {

    private TimeDepositMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TimeDepositMapper();
    }

    @Test
    @DisplayName("toDTO converts entity with withdrawals correctly")
    void toDTO_convertsEntityWithWithdrawals() {
        // Given
        TimeDepositEntity entity = createEntity(1, "premium", 50000.0, 60);
        WithdrawalEntity withdrawal = createWithdrawal(entity, 1000.0, LocalDate.of(2024, 1, 15));
        entity.getWithdrawals().add(withdrawal);

        // When
        TimeDepositResponseDTO dto = mapper.toDTO(entity);

        // Then
        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.planType()).isEqualTo("premium");
        assertThat(dto.balance()).isEqualByComparingTo(BigDecimal.valueOf(50000.0));
        assertThat(dto.days()).isEqualTo(60);
        assertThat(dto.withdrawals()).hasSize(1);
        assertThat(dto.withdrawals().get(0).amount()).isEqualByComparingTo(BigDecimal.valueOf(1000.0));
    }

    @Test
    @DisplayName("toDTO handles null entity")
    void toDTO_handlesNullEntity() {
        // When
        TimeDepositResponseDTO dto = mapper.toDTO(null);

        // Then
        assertThat(dto).isNull();
    }

    @Test
    @DisplayName("toDTOList converts multiple entities")
    void toDTOList_convertsMultipleEntities() {
        // Given
        TimeDepositEntity entity1 = createEntity(1, "basic", 10000.0, 45);
        TimeDepositEntity entity2 = createEntity(2, "student", 5000.0, 100);
        List<TimeDepositEntity> entities = List.of(entity1, entity2);

        // When
        List<TimeDepositResponseDTO> dtos = mapper.toDTOList(entities);

        // Then
        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).planType()).isEqualTo("basic");
        assertThat(dtos.get(1).planType()).isEqualTo("student");
    }

    @Test
    @DisplayName("toDomain converts entity to TimeDeposit")
    void toDomain_convertsEntityToTimeDeposit() {
        // Given
        TimeDepositEntity entity = createEntity(1, "basic", 10000.0, 45);

        // When
        TimeDeposit domain = mapper.toDomain(entity);

        // Then
        assertThat(domain.getId()).isEqualTo(1);
        assertThat(domain.getPlanType()).isEqualTo("basic");
        assertThat(domain.getBalance()).isEqualTo(10000.0);
        assertThat(domain.getDays()).isEqualTo(45);
    }

    @Test
    @DisplayName("toDomain handles null entity")
    void toDomain_handlesNullEntity() {
        // When
        TimeDeposit domain = mapper.toDomain(null);

        // Then
        assertThat(domain).isNull();
    }

    @Test
    @DisplayName("toDomainList converts multiple entities")
    void toDomainList_convertsMultipleEntities() {
        // Given
        TimeDepositEntity entity1 = createEntity(1, "basic", 10000.0, 45);
        TimeDepositEntity entity2 = createEntity(2, "premium", 50000.0, 60);
        List<TimeDepositEntity> entities = List.of(entity1, entity2);

        // When
        List<TimeDeposit> domainList = mapper.toDomainList(entities);

        // Then
        assertThat(domainList).hasSize(2);
        assertThat(domainList.get(0).getPlanType()).isEqualTo("basic");
        assertThat(domainList.get(1).getPlanType()).isEqualTo("premium");
    }

    @Test
    @DisplayName("updateEntityFromDomain updates balance correctly")
    void updateEntityFromDomain_updatesBalanceCorrectly() {
        // Given
        TimeDepositEntity entity = createEntity(1, "basic", 10000.0, 45);
        TimeDeposit domain = new TimeDeposit(1, "basic", 10008.33, 45);

        // When
        mapper.updateEntityFromDomain(entity, domain);

        // Then
        assertThat(entity.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(10008.33));
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

    private WithdrawalEntity createWithdrawal(TimeDepositEntity deposit, double amount, LocalDate date) {
        WithdrawalEntity withdrawal = new WithdrawalEntity();
        withdrawal.setId(1);
        withdrawal.setTimeDeposit(deposit);
        withdrawal.setAmount(BigDecimal.valueOf(amount));
        withdrawal.setWithdrawalDate(date);
        return withdrawal;
    }
}

