package com.lennartmoeller.finance.dto;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CategoryStatsDTOTest {
    @Test
    void gettersAndSetters() {
        CategoryStatsDTO dto = new CategoryStatsDTO();
        CategoryDTO cat = new CategoryDTO();
        RowStatsDTO stats = new RowStatsDTO(Map.of());
        CategoryStatsDTO child = new CategoryStatsDTO();

        dto.setCategory(cat);
        dto.setStats(stats);
        dto.setChildren(List.of(child));

        assertSame(cat, dto.getCategory());
        assertSame(stats, dto.getStats());
        assertEquals(List.of(child), dto.getChildren());
    }
}
