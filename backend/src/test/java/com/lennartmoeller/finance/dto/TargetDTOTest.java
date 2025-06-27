package com.lennartmoeller.finance.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class TargetDTOTest {
    @Test
    void gettersAndSetters() {
        TargetDTO dto = new TargetDTO();
        dto.setId(1L);
        dto.setCategoryId(2L);
        LocalDate start = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2023, 12, 31);
        dto.setStart(start);
        dto.setEnd(end);
        dto.setAmount(50L);

        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getCategoryId());
        assertEquals(start, dto.getStart());
        assertEquals(end, dto.getEnd());
        assertEquals(50L, dto.getAmount());
    }
}
