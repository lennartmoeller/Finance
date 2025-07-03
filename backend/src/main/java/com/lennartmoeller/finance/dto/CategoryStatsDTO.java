package com.lennartmoeller.finance.dto;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
@Setter
public class CategoryStatsDTO {
    private CategoryDTO category;
    private RowStatsDTO stats;
    private List<CategoryStatsDTO> children;
}
