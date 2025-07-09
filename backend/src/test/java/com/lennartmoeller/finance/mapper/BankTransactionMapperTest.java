package com.lennartmoeller.finance.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.lennartmoeller.finance.dto.BankTransactionDTO;
import com.lennartmoeller.finance.dto.CamtV8TransactionDTO;
import com.lennartmoeller.finance.dto.IngV1TransactionDTO;
import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.model.BankType;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

class BankTransactionMapperTest {

    private final BankTransactionMapper mapper = new BankTransactionMapperImpl();

    private static Account account() {
        Account acc = new Account();
        acc.setIban("DE000");
        return acc;
    }

    private static IngV1TransactionDTO ingDto() {
        IngV1TransactionDTO dto = new IngV1TransactionDTO();
        dto.setId(2L);
        dto.setIban("DE000");
        dto.setBookingDate(LocalDate.of(2024, 1, 2));
        dto.setPurpose("p");
        dto.setCounterparty("c");
        dto.setAmount(11L);
        dto.setData(Map.of("k", "v"));
        return dto;
    }

    private static CamtV8TransactionDTO camtDto() {
        CamtV8TransactionDTO dto = new CamtV8TransactionDTO();
        dto.setId(3L);
        dto.setIban("DE000");
        dto.setBookingDate(LocalDate.of(2024, 1, 3));
        dto.setPurpose("p");
        dto.setCounterparty("c");
        dto.setAmount(12L);
        dto.setData(Map.of("k", "v"));
        return dto;
    }

    private static Stream<Arguments> dtoVariants() {
        return Stream.of(Arguments.of(ingDto(), BankType.ING_V1), Arguments.of(camtDto(), BankType.CAMT_V8));
    }

    @Nested
    class ToEntity {
        @ParameterizedTest
        @MethodSource("com.lennartmoeller.finance.mapper.BankTransactionMapperTest#dtoVariants")
        void mapsFieldsFromDifferentDtos(BankTransactionDTO dto, BankType expectedBank) {
            Account acc = account();
            BankTransaction entity;
            if (dto instanceof IngV1TransactionDTO ing) {
                entity = mapper.toEntity(ing, acc);
            } else if (dto instanceof CamtV8TransactionDTO camt) {
                entity = mapper.toEntity(camt, acc);
            } else {
                throw new IllegalStateException("Unexpected DTO type");
            }

            assertThat(entity.getAccount()).isSameAs(acc);
            assertThat(entity.getBookingDate()).isEqualTo(dto.getBookingDate());
            assertThat(entity.getAmount()).isEqualTo(dto.getAmount());
            assertThat(entity.getBank()).isEqualTo(expectedBank);
            assertThat(entity.getData()).isEqualTo(dto.getData());
        }

        @Test
        void returnsNullWhenBothArgumentsAreNull() {
            assertThat(mapper.toEntity((IngV1TransactionDTO) null, null)).isNull();
            assertThat(mapper.toEntity((CamtV8TransactionDTO) null, null)).isNull();
        }
    }

    @Nested
    class ToDto {
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
            entity.setData(new LinkedHashMap<>(Map.of("x", "y")));

            BankTransactionDTO dto = mapper.toDto(entity);

            assertThat(dto.getId()).isEqualTo(entity.getId());
            assertThat(dto.getIban()).isEqualTo(entity.getAccount().getIban());
            assertThat(dto.getBank()).isEqualTo(entity.getBank());
            assertThat(dto.getData()).isEqualTo(entity.getData());
        }

        @ParameterizedTest
        @NullSource
        void returnsNullForNullInput(BankTransaction entity) {
            assertThat(mapper.toDto(entity)).isNull();
        }

        @Test
        void handlesMissingAccountAndData() {
            BankTransaction entity = new BankTransaction();
            entity.setBank(BankType.ING_V1);
            entity.setData(null);

            BankTransactionDTO dto = mapper.toDto(entity);

            assertThat(dto.getIban()).isNull();
            assertThat(dto.getData()).isNull();
        }
    }
}
