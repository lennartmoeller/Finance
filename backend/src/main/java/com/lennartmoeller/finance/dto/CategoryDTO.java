package com.lennartmoeller.finance.dto;

import com.lennartmoeller.finance.model.CategorySmoothType;
import com.lennartmoeller.finance.model.TransactionType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@RequiredArgsConstructor
@Setter
public class CategoryDTO {

	private Long id;
	private Long parentId;
	private String label;
	private TransactionType transactionType;
	private CategorySmoothType smoothType;
	private String icon;
	private List<TargetDTO> targets;

}
