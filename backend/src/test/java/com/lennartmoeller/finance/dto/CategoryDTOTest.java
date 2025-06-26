package com.lennartmoeller.finance.dto;

import com.lennartmoeller.finance.model.CategorySmoothType;
import com.lennartmoeller.finance.model.TransactionType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
