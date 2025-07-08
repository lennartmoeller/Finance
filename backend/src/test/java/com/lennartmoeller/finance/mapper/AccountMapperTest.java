package com.lennartmoeller.finance.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.lennartmoeller.finance.dto.AccountDTO;
import com.lennartmoeller.finance.model.Account;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

class AccountMapperTest {

    private final AccountMapper mapper = new AccountMapperImpl();

    private static Account sampleAccount() {
        Account acc = new Account();
        acc.setId(42L);
        acc.setLabel("Checking");
        acc.setIban("DE123");
        acc.setStartBalance(100L);
        acc.setActive(false);
        acc.setDeposits(true);
        return acc;
    }

    private static AccountDTO sampleDto() {
        AccountDTO dto = new AccountDTO();
        dto.setId(42L);
        dto.setLabel("Checking");
        dto.setIban("DE123");
        dto.setStartBalance(100L);
        dto.setActive(false);
        dto.setDeposits(true);
        return dto;
    }

    @Nested
    class ToDto {
        @Test
        void mapsAllFields() {
            Account account = sampleAccount();

            AccountDTO dto = mapper.toDto(account);

            assertThat(dto).usingRecursiveComparison().isEqualTo(sampleDto());
        }

        @ParameterizedTest
        @NullSource
        void returnsNullWhenInputIsNull(Account input) {
            assertThat(mapper.toDto(input)).isNull();
        }
    }

    @Nested
    class ToEntity {
        @Test
        void mapsAllFields() {
            AccountDTO dto = sampleDto();

            Account entity = mapper.toEntity(dto);

            assertThat(entity).usingRecursiveComparison().isEqualTo(sampleAccount());
        }

        @ParameterizedTest
        @NullSource
        void returnsNullWhenInputIsNull(AccountDTO input) {
            assertThat(mapper.toEntity(input)).isNull();
        }
    }
}
