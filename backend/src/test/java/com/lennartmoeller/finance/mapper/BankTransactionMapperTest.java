package com.lennartmoeller.finance.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.model.BankType;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

class BankTransactionMapperTest {
    private final BankTransactionMapper mapper = new BankTransactionMapperImpl();

    private static Account account() {
        Account acc = new Account();
        acc.setId(5L);
        acc.setIban("DE000");
        return acc;
    }

    @Test
    void mapsEntityFields() {
        BankTransaction entity = new BankTransaction();
        entity.setId(4L);
        entity.setBank(BankType.ING_V1);
        entity.setAccount(account());
        entity.setBookingDate(LocalDate.of(2024, 2, 2));
        entity.setPurpose("p");
        entity.setCounterparty("c");
        entity.setAmount(13L);
        entity.setData("01.01.2024;01.01.2024;Counter;Text;Purpose;100,00;EUR;5,00;EUR");

        var dto = mapper.toDto(entity);

        assertThat(dto.getId()).isEqualTo(entity.getId());
        assertThat(dto.getAccountId()).isEqualTo(entity.getAccount().getId());
        assertThat(dto.getBank()).isEqualTo(entity.getBank());
        assertThat(dto.getAmount()).isEqualTo(entity.getAmount());
    }

    @ParameterizedTest
    @NullSource
    void returnsNullForNullInput(BankTransaction entity) {
        assertThat(mapper.toDto(entity)).isNull();
    }
}
