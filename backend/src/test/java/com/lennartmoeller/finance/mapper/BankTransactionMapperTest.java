package com.lennartmoeller.finance.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.lennartmoeller.finance.dto.BankTransactionDTO;
import com.lennartmoeller.finance.dto.CamtV8TransactionDTO;
import com.lennartmoeller.finance.dto.IngV1TransactionDTO;
import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.model.BankType;
import java.time.LocalDate;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BankTransactionMapperTest {
    private final BankTransactionMapper mapper = new BankTransactionMapperImpl();

    private static Account account() {
        Account acc = new Account();
        acc.setId(10L);
        acc.setIban("DE");
        return acc;
    }

    @Nested
    class ToDto {
        @Test
        void mapsIngEntity() {
            BankTransaction entity = new BankTransaction();
            entity.setId(1L);
            entity.setBank(BankType.ING_V1);
            entity.setAccount(account());
            entity.setBookingDate(LocalDate.of(2024, 1, 1));
            entity.setPurpose("p");
            entity.setCounterparty("c");
            entity.setAmount(5L);
            entity.setData("01.01.2024;02.01.2024;c;text;p;1,00;EUR;5,00;EUR");

            BankTransactionDTO dto = mapper.toDto(entity);

            assertThat(dto).isInstanceOf(IngV1TransactionDTO.class);
            assertThat(dto.getAccountId()).isEqualTo(10L);
            assertThat(dto.getBookingDate()).isEqualTo(entity.getBookingDate());
        }

        @Test
        void mapsCamtEntity() {
            BankTransaction entity = new BankTransaction();
            entity.setId(2L);
            entity.setBank(BankType.CAMT_V8);
            entity.setAccount(account());
            entity.setBookingDate(LocalDate.of(2024, 2, 2));
            entity.setPurpose("p");
            entity.setCounterparty("c");
            entity.setAmount(5L);
            entity.setData("DE;01.01.24;02.01.24;text;p;CID;MID;CR;COL;O;F;CP;IBAN;BIC;1,00;EUR;info");

            BankTransactionDTO dto = mapper.toDto(entity);

            assertThat(dto).isInstanceOf(CamtV8TransactionDTO.class);
            assertThat(dto.getAccountId()).isEqualTo(10L);
            assertThat(dto.getBookingDate()).isEqualTo(entity.getBookingDate());
        }

        @Test
        void returnsNullForNullInput() {
            assertThat(mapper.toDto(null)).isNull();
        }
    }
}
