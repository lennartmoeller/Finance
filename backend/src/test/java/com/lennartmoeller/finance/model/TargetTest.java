package com.lennartmoeller.finance.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class TargetTest {
    @Test
    void testGettersAndSetters() {
        Target target = new Target();
        Category category = new Category();
        target.setCategory(category);
        target.setStartDate(LocalDate.of(2024, 1, 1));
        target.setEndDate(LocalDate.of(2024, 12, 31));
        target.setAmount(100L);

        assertEquals(category, target.getCategory());
        assertEquals(LocalDate.of(2024, 1, 1), target.getStartDate());
        assertEquals(LocalDate.of(2024, 12, 31), target.getEndDate());
        assertEquals(100L, target.getAmount());
    }

    @Test
    void testEqualsAndHashCode() {
        Target t1 = new Target();
        t1.setId(1L);

        Target t2 = new Target();
        t2.setId(1L);

        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());

        t2.setId(2L);
        assertNotEquals(t1, t2);
    }
}
