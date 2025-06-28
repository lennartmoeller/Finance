package com.lennartmoeller.finance.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.lennartmoeller.finance.dto.CamtV8TransactionDTO;
import com.lennartmoeller.finance.dto.IngV1TransactionDTO;
import com.lennartmoeller.finance.model.BankTransaction;
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

        BankTransactionMapper mapper = new BankTransactionMapperImpl();
        BankTransaction entity = mapper.toEntity(dto);

        assertEquals(dto.getIban(), entity.getIban());
        assertEquals(dto.getBookingDate(), entity.getBookingDate());
        assertEquals(dto.getPurpose(), entity.getPurpose());
        assertEquals(dto.getCounterparty(), entity.getCounterparty());
        assertEquals(dto.getAmount(), entity.getAmount());
        assertEquals("ING_V1", entity.getBank().name());
    }

    @Test
    void testCamtV8ToEntity() {
        CamtV8TransactionDTO dto = new CamtV8TransactionDTO();
        dto.setIban("DE456");
        dto.setBookingDate(LocalDate.of(2024, 2, 2));
        dto.setPurpose("p2");
        dto.setCounterparty("c2");
        dto.setAmount(200L);

        BankTransactionMapper mapper = new BankTransactionMapperImpl();
        BankTransaction entity = mapper.toEntity(dto);

        assertEquals("CAMT_V8", entity.getBank().name());
        assertEquals(dto.getIban(), entity.getIban());
    }
}
