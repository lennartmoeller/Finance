package com.lennartmoeller.finance.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CategorySmoothTypeTest {
    @Test
    void testEnumValues() {
        CategorySmoothType[] values = CategorySmoothType.values();
        assertEquals(5, values.length);
        assertEquals(CategorySmoothType.DAILY, values[0]);
        assertEquals(CategorySmoothType.MONTHLY, values[1]);
        assertEquals(CategorySmoothType.QUARTER_YEARLY, values[2]);
        assertEquals(CategorySmoothType.HALF_YEARLY, values[3]);
        assertEquals(CategorySmoothType.YEARLY, values[4]);
    }
}
