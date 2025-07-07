package com.lennartmoeller.finance.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.lennartmoeller.finance.dto.AccountDTO;
import com.lennartmoeller.finance.model.Account;
import org.junit.jupiter.api.Test;

class AccountMapperTest {
    private final AccountMapper mapper = new AccountMapperImpl();

    @Test
    void testToDtoAndBack() {
        Account account = new Account();
        account.setId(1L);
        account.setLabel("Checking");
        account.setIban("DE123");
        account.setStartBalance(500L);
        account.setActive(false);
        account.setDeposits(true);

        AccountDTO dto = mapper.toDto(account);
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(account.getId());
        assertThat(dto.getLabel()).isEqualTo(account.getLabel());
        assertThat(dto.getIban()).isEqualTo(account.getIban());
        assertThat(dto.getStartBalance()).isEqualTo(account.getStartBalance());
        assertThat(dto.getActive()).isEqualTo(account.getActive());
        assertThat(dto.getDeposits()).isEqualTo(account.getDeposits());

        Account entity = mapper.toEntity(dto);
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(account.getId());
        assertThat(entity.getLabel()).isEqualTo(account.getLabel());
        assertThat(entity.getIban()).isEqualTo(account.getIban());
        assertThat(entity.getStartBalance()).isEqualTo(account.getStartBalance());
        assertThat(entity.getActive()).isEqualTo(account.getActive());
        assertThat(entity.getDeposits()).isEqualTo(account.getDeposits());
    }

    @Test
    void testNullValues() {
        assertThat(mapper.toDto(null)).isNull();
        assertThat(mapper.toEntity(null)).isNull();
    }
}
