package com.lennartmoeller.finance.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.lennartmoeller.finance.dto.BankTransactionDTO;
import com.lennartmoeller.finance.dto.CamtV8TransactionDTO;
import com.lennartmoeller.finance.dto.IngV1TransactionDTO;
import com.lennartmoeller.finance.model.Account;
import com.lennartmoeller.finance.model.BankTransaction;
import com.lennartmoeller.finance.model.BankType;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class BankTransactionMapperTest {
    @Test
    void testIngV1ToEntity() {
        IngV1TransactionDTO dto = new IngV1TransactionDTO();
        dto.setIban("DE123");
        dto.setBookingDate(LocalDate.of(2024, 1, 1));
        dto.setPurpose("p");
        dto.setCounterparty("c");
        dto.setAmount(100L);
        dto.setData(new java.util.HashMap<>());
        dto.getData().put("a", "b");

        Account acc = new Account();

        BankTransactionMapper mapper = new BankTransactionMapperImpl();
        BankTransaction entity = mapper.toEntity(dto, acc);

        assertThat(entity.getAccount()).isSameAs(acc);
        assertThat(entity.getBookingDate()).isEqualTo(dto.getBookingDate());
        assertThat(entity.getPurpose()).isEqualTo(dto.getPurpose());
        assertThat(entity.getCounterparty()).isEqualTo(dto.getCounterparty());
        assertThat(entity.getAmount()).isEqualTo(dto.getAmount());
        assertThat(entity.getBank()).isEqualTo(BankType.ING_V1);
        assertThat(entity.getData()).isEqualTo(dto.getData());
    }

    @Test
    void testCamtV8ToEntity() {
        CamtV8TransactionDTO dto = new CamtV8TransactionDTO();
        dto.setIban("DE456");
        dto.setBookingDate(LocalDate.of(2024, 2, 2));
        dto.setPurpose("p2");
        dto.setCounterparty("c2");
        dto.setAmount(200L);
        dto.setData(new java.util.HashMap<>());
        dto.getData().put("x", "y");

        Account acc = new Account();

        BankTransactionMapper mapper = new BankTransactionMapperImpl();
        BankTransaction entity = mapper.toEntity(dto, acc);

        assertThat(entity.getBank()).isEqualTo(BankType.CAMT_V8);
        assertThat(entity.getAccount()).isSameAs(acc);
        assertThat(entity.getData()).isEqualTo(dto.getData());
    }

    @Test
    void testToDto() {
        BankTransaction entity = new BankTransaction();
        Account account = new Account();
        account.setIban("DE123");
        entity.setAccount(account);
        entity.setId(3L);
        entity.setBank(BankType.ING_V1);
        entity.setBookingDate(LocalDate.of(2024, 3, 3));
        entity.setPurpose("p");
        entity.setCounterparty("c");
        entity.setAmount(10L);
        entity.getData().put("k", "v");

        BankTransactionDTO dto = new BankTransactionMapperImpl().toDto(entity);

        assertThat(dto.getId()).isEqualTo(entity.getId());
        assertThat(dto.getIban()).isEqualTo(account.getIban());
        assertThat(dto.getData()).isEqualTo(entity.getData());
    }

    @Test
    void testBaseToEntity() {
        BankTransactionDTO dto = new BankTransactionDTO();
        dto.setId(5L);
        dto.setBank(BankType.CAMT_V8);
        dto.setIban("DE");
        dto.setBookingDate(LocalDate.of(2024, 4, 4));
        dto.setPurpose("p");
        dto.setCounterparty("c");
        dto.setAmount(50L);
        dto.setData(new java.util.HashMap<>());
        dto.getData().put("k", "v");

        Account acc = new Account();

        BankTransaction entity = new BankTransactionMapperImpl().toEntity(dto, acc);
        assertThat(entity.getBank()).isEqualTo(dto.getBank());
        assertThat(entity.getAccount()).isSameAs(acc);
        assertThat(entity.getData()).isEqualTo(dto.getData());
    }

    @Test
    void nullInputsReturnNull() {
        BankTransactionMapper mapper = new BankTransactionMapperImpl();
        assertThat(mapper.toDto(null)).isNull();
        assertThat(mapper.toEntity((BankTransactionDTO) null, null)).isNull();
        assertThat(mapper.toEntity((IngV1TransactionDTO) null, null)).isNull();
        assertThat(mapper.toEntity((CamtV8TransactionDTO) null, null)).isNull();
    }

    @Test
    void handlesNullDataFields() {
        BankTransaction entity = new BankTransaction();
        entity.setData(null);
        BankTransactionDTO dto = new BankTransactionMapperImpl().toDto(entity);
        assertThat(dto.getData()).isNull();

        BankTransactionDTO base = new BankTransactionDTO();
        base.setData(null);
        BankTransaction mapped = new BankTransactionMapperImpl().toEntity(base, null);
        assertThat(mapped.getData()).isEmpty();

        IngV1TransactionDTO ing = new IngV1TransactionDTO();
        ing.setData(null);
        assertThat(new BankTransactionMapperImpl().toEntity(ing, null).getData())
                .isEmpty();

        CamtV8TransactionDTO camt = new CamtV8TransactionDTO();
        camt.setData(null);
        assertThat(new BankTransactionMapperImpl().toEntity(camt, null).getData())
                .isEmpty();
    }

    @Test
    void createsEntityWhenOnlyAccountProvided() {
        Account account = new Account();
        BankTransaction entity = new BankTransactionMapperImpl().toEntity((BankTransactionDTO) null, account);
        assertThat(entity).isNotNull();
        assertThat(entity.getAccount()).isSameAs(account);
        assertThat(entity.getBank()).isNull();
    }
}
