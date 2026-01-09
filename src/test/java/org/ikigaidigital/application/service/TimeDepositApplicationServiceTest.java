package org.ikigaidigital.application.service;

import org.ikigaidigital.application.port.input.UpdateAllBalancesUseCase;
import org.ikigaidigital.application.port.output.TimeDepositPersistencePort;
import org.ikigaidigital.domain.model.TimeDeposit;
import org.ikigaidigital.domain.model.TimeDepositCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TimeDepositApplicationService Unit Tests")
class TimeDepositApplicationServiceTest {

    @Mock
    private TimeDepositPersistencePort persistencePort;

    @Mock
    private TimeDepositCalculator timeDepositCalculator;

    private TimeDepositApplicationService service;

    @BeforeEach
    void setUp() {
        service = new TimeDepositApplicationService(persistencePort, timeDepositCalculator);
    }

    @Nested
    @DisplayName("getAllTimeDeposits()")
    class GetAllTimeDeposits {

        @Test
        @DisplayName("returns deposits from persistence port")
        void returnsDepositsFromPersistencePort() {
            // Given
            TimeDeposit deposit = new TimeDeposit(1, "basic", 10000.00, 45);
            when(persistencePort.findAllWithWithdrawals()).thenReturn(List.of(deposit));

            // When
            List<TimeDeposit> result = service.getAllTimeDeposits();

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(1);
            assertThat(result.get(0).getPlanType()).isEqualTo("basic");
            verify(persistencePort).findAllWithWithdrawals();
        }

        @Test
        @DisplayName("returns empty list when no deposits exist")
        void returnsEmptyListWhenNoDeposits() {
            // Given
            when(persistencePort.findAllWithWithdrawals()).thenReturn(Collections.emptyList());

            // When
            List<TimeDeposit> result = service.getAllTimeDeposits();

            // Then
            assertThat(result).isEmpty();
            verify(persistencePort).findAllWithWithdrawals();
        }
    }

    @Nested
    @DisplayName("updateAllBalances()")
    class UpdateAllBalances {

        @Test
        @DisplayName("calls calculator and saves deposits")
        void callsCalculatorAndSavesDeposits() {
            // Given
            TimeDeposit deposit = new TimeDeposit(1, "basic", 10000.00, 45);
            List<TimeDeposit> deposits = new ArrayList<>(List.of(deposit));
            when(persistencePort.findAll()).thenReturn(deposits);
            when(persistencePort.saveAll(deposits)).thenReturn(deposits);

            // When
            UpdateAllBalancesUseCase.UpdateBalancesResult result = service.updateAllBalances();

            // Then
            assertThat(result.updatedCount()).isEqualTo(1);
            verify(persistencePort).findAll();
            verify(timeDepositCalculator).updateBalance(deposits);
            verify(persistencePort).saveAll(deposits);
        }

        @Test
        @DisplayName("returns zero count for empty list")
        void returnsZeroCountForEmptyList() {
            // Given
            when(persistencePort.findAll()).thenReturn(Collections.emptyList());
            when(persistencePort.saveAll(anyList())).thenReturn(Collections.emptyList());

            // When
            UpdateAllBalancesUseCase.UpdateBalancesResult result = service.updateAllBalances();

            // Then
            assertThat(result.updatedCount()).isEqualTo(0);
            verify(timeDepositCalculator).updateBalance(Collections.emptyList());
        }

        @Test
        @DisplayName("processes multiple deposits")
        void processesMultipleDeposits() {
            // Given
            List<TimeDeposit> deposits = new ArrayList<>(List.of(
                    new TimeDeposit(1, "basic", 10000.00, 45),
                    new TimeDeposit(2, "student", 5000.00, 100),
                    new TimeDeposit(3, "premium", 50000.00, 60)
            ));
            when(persistencePort.findAll()).thenReturn(deposits);
            when(persistencePort.saveAll(deposits)).thenReturn(deposits);

            // When
            UpdateAllBalancesUseCase.UpdateBalancesResult result = service.updateAllBalances();

            // Then
            assertThat(result.updatedCount()).isEqualTo(3);
            verify(timeDepositCalculator).updateBalance(deposits);
        }
    }
}

