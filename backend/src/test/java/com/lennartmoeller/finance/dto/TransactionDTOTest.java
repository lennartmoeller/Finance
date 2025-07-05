package com.lennartmoeller.finance.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class TransactionDTOTest {
    @Test
    void gettersAndSetters() {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(1L);
        dto.setAccountId(2L);
        dto.setCategoryId(3L);
        LocalDate d = LocalDate.of(2023, 6, 15);
        dto.setDate(d);
        dto.setAmount(100L);
        dto.setDescription("desc");
        dto.setPinned(true);

        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getAccountId());
        assertEquals(3L, dto.getCategoryId());
        assertEquals(d, dto.getDate());
        assertEquals(100L, dto.getAmount());
        assertEquals("desc", dto.getDescription());
        assertEquals(true, dto.getPinned());
    }
}
