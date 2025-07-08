package com.lennartmoeller.finance.dto;

import java.util.List;
import lombok.Data;

@Data
public class CategoryStatsDTO {
    private CategoryDTO category;
    private RowStatsDTO stats;
    private List<CategoryStatsDTO> children;
}
