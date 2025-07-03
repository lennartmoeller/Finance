package com.lennartmoeller.finance.mapper;

import static org.junit.jupiter.api.Assertions.*;

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

        BankTransactionMapper mapper = new BankTransactionMapperImpl();
        BankTransaction entity = mapper.toEntity(dto);

        assertNull(entity.getAccount());
        assertEquals(dto.getBookingDate(), entity.getBookingDate());
        assertEquals(dto.getPurpose(), entity.getPurpose());
        assertEquals(dto.getCounterparty(), entity.getCounterparty());
        assertEquals(dto.getAmount(), entity.getAmount());
        assertEquals("ING_V1", entity.getBank().name());
        assertEquals(dto.getData(), entity.getData());
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

        BankTransactionMapper mapper = new BankTransactionMapperImpl();
        BankTransaction entity = mapper.toEntity(dto);

        assertEquals("CAMT_V8", entity.getBank().name());
        assertNull(entity.getAccount());
        assertEquals(dto.getData(), entity.getData());
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

        assertEquals(entity.getId(), dto.getId());
        assertEquals(account.getIban(), dto.getIban());
        assertEquals(entity.getData(), dto.getData());
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

        BankTransaction entity = new BankTransactionMapperImpl().toEntity(dto);
        assertEquals(dto.getBank(), entity.getBank());
        assertNull(entity.getAccount());
        assertEquals(dto.getData(), entity.getData());
    }

    @Test
    void nullInputsReturnNull() {
        BankTransactionMapper mapper = new BankTransactionMapperImpl();
        assertNull(mapper.toDto(null));
        assertNull(mapper.toEntity((BankTransactionDTO) null));
        assertNull(mapper.toEntity((IngV1TransactionDTO) null));
        assertNull(mapper.toEntity((CamtV8TransactionDTO) null));
    }

    @Test
    void handlesNullDataFields() {
        BankTransaction entity = new BankTransaction();
        entity.setData(null);
        BankTransactionDTO dto = new BankTransactionMapperImpl().toDto(entity);
        assertNull(dto.getData());

        BankTransactionDTO base = new BankTransactionDTO();
        base.setData(null);
        BankTransaction mapped = new BankTransactionMapperImpl().toEntity(base);
        assertTrue(mapped.getData().isEmpty());

        IngV1TransactionDTO ing = new IngV1TransactionDTO();
        ing.setData(null);
        assertTrue(new BankTransactionMapperImpl().toEntity(ing).getData().isEmpty());

        CamtV8TransactionDTO camt = new CamtV8TransactionDTO();
        camt.setData(null);
        assertTrue(new BankTransactionMapperImpl().toEntity(camt).getData().isEmpty());
    }
}
