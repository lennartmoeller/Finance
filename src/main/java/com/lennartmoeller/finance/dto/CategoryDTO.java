package com.lennartmoeller.finance.dto;

import com.lennartmoeller.finance.model.CategorySmoothType;
import com.lennartmoeller.finance.model.TransactionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class CategoryDTO {
	private Long id;
	private Long parentId;
	private String label;
	private TransactionType transactionType;
	private CategorySmoothType smoothType;
	private List<TargetDTO> targets;
}
