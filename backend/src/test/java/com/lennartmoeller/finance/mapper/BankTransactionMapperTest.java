package com.lennartmoeller.finance.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lennartmoeller.finance.dto.BankTransactionDTO;
import com.lennartmoeller.finance.dto.CamtV8TransactionDTO;
import com.lennartmoeller.finance.dto.IngV1TransactionDTO;
import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.model.BankType;
import java.time.LocalDate;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class BankTransactionMapperTest {
    private final BankTransactionMapper mapper = new BankTransactionMapperImpl();

    private static Account account() {
        Account acc = new Account();
        acc.setId(10L);
        acc.setIban("DE");
        return acc;
    }

    private static BankTransaction createTransaction(BankType type, String data) {
        BankTransaction entity = new BankTransaction();
        entity.setId(1L);
        entity.setBank(type);
        entity.setAccount(account());
        entity.setBookingDate(LocalDate.of(2024, 1, 1));
        entity.setPurpose("p");
        entity.setCounterparty("c");
        entity.setAmount(5L);
        entity.setData(data);
        return entity;
    }

    private static Stream<Arguments> dtoMappingArguments() {
        return Stream.of(
                Arguments.of(
                        createTransaction(BankType.ING_V1, "01.01.2024;02.01.2024;c;text;p;1,00;EUR;5,00;EUR"),
                        IngV1TransactionDTO.class),
                Arguments.of(
                        createTransaction(
                                BankType.CAMT_V8,
                                "DE;01.01.24;02.01.24;text;p;CID;MID;CR;COL;O;F;CP;IBAN;BIC;1,00;EUR;info"),
                        CamtV8TransactionDTO.class));
    }

    @Nested
    class ToDto {
        @ParameterizedTest
        @MethodSource("com.lennartmoeller.finance.mapper.BankTransactionMapperTest#dtoMappingArguments")
        void mapsEntity(BankTransaction entity, Class<? extends BankTransactionDTO> expectedClass) {
            BankTransactionDTO dto = mapper.toDto(entity);

            assertThat(dto).isInstanceOf(expectedClass);
            assertThat(dto.getAccountId()).isEqualTo(entity.getAccount().getId());
            assertThat(dto.getBookingDate()).isEqualTo(entity.getBookingDate());
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "abc"})
        void throwsForInvalidAmounts(String amount) {
            BankTransaction entity =
                    createTransaction(BankType.ING_V1, "01.01.2024;02.01.2024;c;text;p;" + amount + ";EUR;5,00;EUR");

            assertThatThrownBy(() -> mapper.toDto(entity)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void returnsNullForNullInput() {
            assertThat(mapper.toDto(null)).isNull();
        }
    }
}
