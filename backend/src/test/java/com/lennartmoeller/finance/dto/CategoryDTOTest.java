package com.lennartmoeller.finance.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lennartmoeller.finance.model.CategorySmoothType;
import com.lennartmoeller.finance.model.TransactionType;
import java.util.List;
import org.junit.jupiter.api.Test;

class CategoryDTOTest {
    @Test
    void gettersAndSetters() {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(1L);
        dto.setParentId(2L);
        dto.setLabel("Food");
        dto.setTransactionType(TransactionType.EXPENSE);
        dto.setSmoothType(CategorySmoothType.MONTHLY);
        dto.setIcon("utensils");
        TargetDTO target = new TargetDTO();
        target.setAmount(100L);
        dto.setTargets(List.of(target));

        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getParentId());
        assertEquals("Food", dto.getLabel());
        assertEquals(TransactionType.EXPENSE, dto.getTransactionType());
        assertEquals(CategorySmoothType.MONTHLY, dto.getSmoothType());
        assertEquals("utensils", dto.getIcon());
        assertEquals(List.of(target), dto.getTargets());
    }
}
