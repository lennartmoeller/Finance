package com.lennartmoeller.finance.testbuilder;

import com.lennartmoeller.finance.dto.AccountDTO;

public final class AccountDTOBuilder {
    private final AccountDTO dto = new AccountDTO();

    private AccountDTOBuilder() {}

    public static AccountDTOBuilder anAccount() {
        return new AccountDTOBuilder();
    }

    public AccountDTOBuilder withId(Long id) {
        dto.setId(id);
        return this;
    }

    public AccountDTOBuilder withLabel(String label) {
        dto.setLabel(label);
        return this;
    }

    public AccountDTOBuilder withIban(String iban) {
        dto.setIban(iban);
        return this;
    }

    public AccountDTOBuilder withStartBalance(Long balance) {
        dto.setStartBalance(balance);
        return this;
    }

    public AccountDTOBuilder withActive(Boolean active) {
        dto.setActive(active);
        return this;
    }

    public AccountDTOBuilder withDeposits(Boolean deposits) {
        dto.setDeposits(deposits);
        return this;
    }

    public AccountDTO build() {
        return dto;
    }
}
