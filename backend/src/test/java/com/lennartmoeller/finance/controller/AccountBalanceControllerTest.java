package com.lennartmoeller.finance.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lennartmoeller.finance.dto.AccountBalanceDTO;
import com.lennartmoeller.finance.service.AccountBalanceService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountBalanceControllerTest {
    @Mock
    private AccountBalanceService service;

    @InjectMocks
    private AccountBalanceController controller;

    @Test
    void shouldReturnAllBalances() {
        List<AccountBalanceDTO> balances = List.of(new AccountBalanceDTO());
        when(service.findAll()).thenReturn(balances);

        List<AccountBalanceDTO> result = controller.getAccountBalances();

        assertThat(result).isEqualTo(balances);
        verify(service).findAll();
    }
}
